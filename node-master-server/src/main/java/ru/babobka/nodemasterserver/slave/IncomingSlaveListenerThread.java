package ru.babobka.nodemasterserver.slave;

import lombok.NonNull;
import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.service.MasterAuthService;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.network.SecureNodeConnection;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeutils.thread.CyclicThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class IncomingSlaveListenerThread extends CyclicThread {

    private final ServerSocket serverSocket;
    private final NodeConnectionFactory nodeConnectionFactory = Container.getInstance().get(NodeConnectionFactory.class);
    private final SlaveFactory slaveFactory = Container.getInstance().get(SlaveFactory.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final MasterAuthService authService = Container.getInstance().get(MasterAuthService.class);
    private final MasterServerConfig config = Container.getInstance().get(MasterServerConfig.class);
    private final TaskPool taskPool = Container.getInstance().get(MasterServerKey.MASTER_SERVER_TASK_POOL);
    private final Sessions sessions = Container.getInstance().get(Sessions.class);

    public IncomingSlaveListenerThread(@NonNull ServerSocket serverSocket) {
        if (serverSocket.isClosed()) {
            throw new IllegalArgumentException("serverSocket is closed");
        }
        this.serverSocket = serverSocket;
        setName("incoming slave listener thread");
    }

    @Override
    public void onCycle() {
        NodeConnection connection = null;
        try {
            Socket socket = serverSocket.accept();
            connection = nodeConnectionFactory.create(socket);
            connection.setReadTimeOut(config.getTimeouts().getAuthTimeOutMillis());
            nodeLogger.info("new connection");
            AuthResult authResult = authService.authClient(connection);
            if (!authResult.isSuccess()) {
                nodeLogger.warning("auth fail");
                connection.close();
                return;
            }
            nodeLogger.info("new slave was successfully authenticated");
            Set<String> availableTasks = connection.receive();
            boolean containsAnyOfTask = taskPool.containsAnyOfTask(availableTasks);
            if (!containsAnyOfTask) {
                nodeLogger.error("new slave doesn't have any common tasks with master");
                fail(connection);
                return;
            }
            success(connection);
            if (!authService.authServer(connection)) {
                nodeLogger.error("server authentication fail");
                connection.close();
                return;
            }
            if (!isAbleToRunNewSlave(authResult)) {
                nodeLogger.error("not able to create session for user " + authResult.getUserName());
                fail(connection);
                return;
            }
            SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, authResult.getSecretKey());
            if (!runNewSlave(availableTasks, authResult.getUserName(), secureNodeConnection)) {
                nodeLogger.warning("cannot run slave");
                sessions.remove(authResult.getUserName());
                fail(connection);
                return;
            }
            connection.setReadTimeOut(config.getTimeouts().getRequestTimeOutMillis());
            success(connection);
        } catch (IOException e) {
            if (!serverSocket.isClosed() || !Thread.currentThread().isInterrupted()) {
                nodeLogger.error(e);
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    boolean isAbleToRunNewSlave(AuthResult authResult) {
        return config.getModes().isSingleSessionMode() ? sessions.put(authResult.getUserName()) : true;
    }

    boolean runNewSlave(Set<String> availableTasks, String userName, SecureNodeConnection secureNodeConnection) {
        try {
            Slave slave = slaveFactory.create(availableTasks, secureNodeConnection, () -> {
                if (config.getModes().isSingleSessionMode()) {
                    sessions.remove(userName);
                }
            });
            if (slavesStorage.add(slave)) {
                slave.start();
                return true;
            }
            return false;
        } catch (RuntimeException e) {
            nodeLogger.error("cannot run new slave");
            return false;
        }
    }

    void fail(NodeConnection connection) throws IOException {
        try {
            connection.send(false);
        } finally {
            connection.close();
        }
    }

    void success(NodeConnection connection) throws IOException {
        connection.send(true);
    }

    @Override
    public void onExit() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            nodeLogger.error(e);
        }
        nodeLogger.debug(this.getClass().getSimpleName() + " is done");
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            nodeLogger.error(e);
        }
    }
}