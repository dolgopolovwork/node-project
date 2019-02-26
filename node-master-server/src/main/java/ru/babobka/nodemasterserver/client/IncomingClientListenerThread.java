package ru.babobka.nodemasterserver.client;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static ru.babobka.nodeserials.enumerations.RequestStatus.NORMAL;
import static ru.babobka.nodeserials.enumerations.RequestStatus.RACE;

/**
 * Created by 123 on 28.10.2017.
 */
public class IncomingClientListenerThread extends Thread {

    private static final Logger logger = Logger.getLogger(IncomingClientListenerThread.class);
    private final ExecutorService executorService = Container.getInstance().get(MasterServerKey.CLIENTS_THREAD_POOL);
    private final NodeConnectionFactory nodeConnectionFactory = Container.getInstance().get(NodeConnectionFactory.class);
    private final ServerSocket serverSocket;

    public IncomingClientListenerThread(@NonNull ServerSocket serverSocket) {
        if (serverSocket.isClosed()) {
            throw new IllegalArgumentException("serverSocket is closed");
        }
        this.serverSocket = serverSocket;
        setName("incoming client listener thread");
    }

    @Override
    public void run() {
        try {
            while (!isDone(serverSocket)) {
                processConnection(serverSocket);
            }
        } finally {
            onExit();
            logger.debug(this.getClass().getSimpleName() + " is done");
        }
    }

    void onExit() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("exception thrown", e);
        }
        executorService.shutdownNow();
    }

    @Override
    public void interrupt() {
        onExit();
        super.interrupt();
    }

    void processConnection(@NonNull ServerSocket serverSocket) {
        if (serverSocket.isClosed()) {
            throw new IllegalStateException("serverSocket is closed");
        }
        try {
            NodeConnection nodeConnection = nodeConnectionFactory.create(serverSocket.accept());
            List<NodeRequest> requests = nodeConnection.receive();
            handleRequest(nodeConnection, requests);
        } catch (IOException e) {
            logger.error("exception thrown", e);
        }
    }

    void handleRequest(NodeConnection nodeConnection, List<NodeRequest> requests) {
        for (NodeRequest request : requests) {
            RequestStatus status = request.getRequestStatus();
            if (!(status == NORMAL || status == RACE)) {
                logger.warn("cannot handle request " + request);
                return;
            }
        }
        try {
            executorService.submit(createClientExecutor(nodeConnection, requests));
        } catch (RuntimeException e) {
            logger.error("error occurred while handling requests " + requests, e);
        }
    }

    Client createClientExecutor(NodeConnection connection, List<NodeRequest> requests) {
        return new Client(connection, requests);
    }

    boolean isDone(@NonNull ServerSocket serverSocket) {
        return serverSocket.isClosed() || Thread.currentThread().isInterrupted();
    }

}
