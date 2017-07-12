package ru.babobka.nodemasterserver.slave;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.exception.EmptyClusterException;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodemasterserver.model.AuthResult;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.service.AuthService;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodemasterserver.service.NodeUsersService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.crypto.RSA;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class Slave extends Thread {

    private final RSA rsa;

    private volatile AuthResult authResult;

    private static final int RSA_KEY_BIT_LENGTH = 256;

    private final NodeUsersService userService = Container.getInstance().get(NodeUsersService.class);

    private final DistributionService distributionService = Container.getInstance().get(DistributionService.class);

    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    private final AuthService authService = Container.getInstance().get(AuthService.class);

    private final Map<UUID, NodeRequest> requestMap = new ConcurrentHashMap<>();

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    private final ResponseStorage responseStorage = Container.getInstance().get(ResponseStorage.class);

    private final MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);

    private final Socket socket;

    public Slave(Socket socket) {
        if (socket != null) {
            logger.info("New connection " + socket);
            this.socket = socket;
            this.rsa = new RSA(RSA_KEY_BIT_LENGTH);
        } else {
            throw new IllegalArgumentException("Socket can not be null");
        }
    }

    public synchronized void sendRequest(NodeRequest request) throws IOException {
        logger.info("sendRequest " + request);
        if (!(request.isRaceStyle() && requestMap.containsKey(request.getTaskId()))) {
            requestMap.put(request.getRequestId(), request);
            StreamUtil.sendObject(request, socket);
            logger.info(request + " was sent");
            userService.incrementTaskCount(authResult.getLogin());
        } else {
            logger.info("Request  " + request + " was ignored due to race style");
            responseStorage.get(request.getTaskId()).add(NodeResponse.dummy(request.getTaskId()));
        }
    }

    private synchronized void setBadAndCancelAllTheRequests() {
        if (!requestMap.isEmpty()) {
            NodeRequest request;
            for (Map.Entry<UUID, NodeRequest> requestEntry : requestMap.entrySet()) {
                request = requestEntry.getValue();
                responseStorage.addBadResponse(request.getTaskId());
                try {
                    distributionService.broadcastStopRequests(slavesStorage.getListByTaskId(request.getTaskId()),
                            NodeRequest.stop(request.getTaskId(), request.getTaskName()));
                } catch (EmptyClusterException e) {
                    logger.error(e);
                }
            }
            requestMap.clear();
        }
    }

    private synchronized void setBadAllTheRequests() {
        if (!requestMap.isEmpty()) {
            NodeRequest request;
            for (Map.Entry<UUID, NodeRequest> requestEntry : requestMap.entrySet()) {
                request = requestEntry.getValue();
                responseStorage.addBadResponse(request.getTaskId());
            }
            logger.info("Responses are clear");
            requestMap.clear();
        }
    }

    public void sendHeartBeating() throws IOException {
        StreamUtil.sendObject(NodeRequest.heartBeatRequest(), socket);
    }

    public synchronized void sendStopRequest(NodeRequest stopRequest) throws IOException {
        for (Map.Entry<UUID, NodeRequest> requestEntry : requestMap.entrySet()) {
            if (requestEntry.getValue().getTaskId().equals(stopRequest.getTaskId())) {
                responseStorage.addStopResponse(stopRequest.getTaskId());
                requestMap.remove(requestEntry.getValue().getRequestId());
            }
        }
        StreamUtil.sendObject(stopRequest, socket);

    }

    private boolean fit() throws IOException {
        boolean fittable = slavesStorage.add(this);
        StreamUtil.sendObject(fittable, this.getSocket());
        return fittable;
    }

    private AuthResult auth() throws SocketException {
        socket.setSoTimeout(masterServerConfig.getAuthTimeOutMillis());
        this.authResult = authService.getAuthResult(rsa, socket);
        if (!authResult.isValid()) {
            slavesStorage.remove(this);
            logger.warning("Can not auth " + socket);
        } else {
            logger.info(authResult.getLogin() + " from " + socket + " was logged");
        }
        return authResult;
    }

    @Override
    public void run() {
        try {
            if (fit() && auth().isValid()) {
                while (!Thread.currentThread().isInterrupted()) {
                    socket.setSoTimeout(masterServerConfig.getRequestTimeOutMillis());
                    NodeResponse response = StreamUtil.receiveObject(socket);
                    if (!response.isHeartBeatingResponse()) {
                        logger.info(response);
                        requestMap.remove(response.getResponseId());
                        logger.info("Remove response " + response.getResponseId());
                        if (responseStorage.exists(response.getTaskId())) {
                            responseStorage.get(response.getTaskId()).add(response);
                        }
                    }
                }
            }
        } catch (IOException e) {
            if (!Thread.currentThread().isInterrupted()) {
                logger.error(e);
            }
            logger.warning("Connection is closed " + socket);
        } catch (RuntimeException e) {
            logger.error(e);
        } finally {
            logger.info("Removing connection " + socket);
            slavesStorage.remove(this);
            synchronized (Slave.class) {
                if (!requestMap.isEmpty()) {
                    logger.info("Slave has a requests to redistribute");
                    try {
                        distributionService.redistribute(this);
                    } catch (DistributionException e) {
                        logger.error(e);
                        setBadAndCancelAllTheRequests();
                    } catch (EmptyClusterException e) {
                        logger.error(e);
                        setBadAllTheRequests();
                    }
                }
            }
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
            if (authResult != null && authResult.getLogin() != null)
                logger.info("User " + authResult.getLogin() + " was disconnected");
        }
    }



    public Map<String, LinkedList<NodeRequest>> getRequestsGroupedByTask() {
        Map<String, LinkedList<NodeRequest>> requestsByTaskName = new HashMap<>();
        for (Map.Entry<UUID, NodeRequest> requestEntry : this.getRequestMap().entrySet()) {
            if (requestsByTaskName.containsKey(requestEntry.getValue().getTaskName())) {
                requestsByTaskName.get(requestEntry.getValue().getTaskName()).add(requestEntry.getValue());
            } else {
                requestsByTaskName.put(requestEntry.getValue().getTaskName(), new LinkedList<>());
                requestsByTaskName.get(requestEntry.getValue().getTaskName()).add(requestEntry.getValue());
            }
        }
        return requestsByTaskName;
    }

    @Override
    public String toString() {
        return "requests " + getRequestCount();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            logger.error(e);
        }

    }

    public int getRequestCount() {
        return requestMap.size();
    }

    public Set<String> getAvailableTasksSet() {
        return authResult.getTaskSet();
    }

    public String getLogin() {
        return authResult.getLogin();
    }

    public Socket getSocket() {
        return socket;
    }

    public Map<UUID, NodeRequest> getRequestMap() {
        return requestMap;
    }

}