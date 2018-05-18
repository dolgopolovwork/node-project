package ru.babobka.nodemasterserver.slave;

import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.service.MasterAuthService;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.network.SecureNodeConnection;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
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
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final MasterAuthService authService = Container.getInstance().get(MasterAuthService.class);
    private final MasterServerConfig config = Container.getInstance().get(MasterServerConfig.class);
    private final TaskPool taskPool = Container.getInstance().get(MasterServerKey.MASTER_SERVER_TASK_POOL);
    private final Sessions sessions = Container.getInstance().get(Sessions.class);

    public IncomingSlaveListenerThread(ServerSocket serverSocket) {
        if (serverSocket == null) {
            throw new IllegalArgumentException("serverSocket is null");
        } else if (serverSocket.isClosed()) {
            throw new IllegalArgumentException("serverSocket is closed");
        }
        this.serverSocket = serverSocket;
    }

    @Override
    public void onCycle() {
        NodeConnection connection = null;
        try {
            Socket socket = serverSocket.accept();
            connection = nodeConnectionFactory.create(socket);
            connection.setReadTimeOut(config.getTimeouts().getAuthTimeOutMillis());
            logger.info("new connection");
            AuthResult authResult = authService.auth(connection);
            if (!authResult.isSuccess()) {
                logger.warning("auth fail");
                connection.close();
                return;
            }
            logger.info("new slave was successfully authenticated");
            Set<String> availableTasks = connection.receive();
            boolean containsAnyOfTask = taskPool.containsAnyOfTask(availableTasks);
            if (!containsAnyOfTask) {
                logger.error("new slave doesn't have any common tasks with master");
                fail(connection);
                return;
            } else {
                success(connection);
            }
            if (!isAbleToRunNewSlave(authResult)) {
                fail(connection);
                logger.error("not able to create session for user " + authResult.getUserName());
                return;
            }
            SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, authResult.getSecretKey());
            if (runNewSlave(availableTasks, authResult.getUserName(), secureNodeConnection)) {
                connection.setReadTimeOut(config.getTimeouts().getRequestTimeOutMillis());
                success(connection);
            } else {
                sessions.remove(authResult.getUserName());
                fail(connection);
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed() || !Thread.currentThread().isInterrupted()) {
                logger.error(e);
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    boolean isAbleToRunNewSlave(AuthResult authResult) {
        return config.getModes().isSingleSessionMode() ? sessions.put(authResult.getUserName()) : true;
    }

    private boolean runNewSlave(Set<String> availableTasks, String userName, SecureNodeConnection secureNodeConnection) {
        try {
            Slave slave = slaveFactory.create(availableTasks, secureNodeConnection, () -> {
                if (config.getModes().isSingleSessionMode()) {
                    sessions.remove(userName);
                }
            });
            slavesStorage.add(slave);
            slave.start();
            return true;
        } catch (RuntimeException e) {
            logger.error("cannot run new slave");
            return false;
        }
    }

    private void fail(NodeConnection connection) throws IOException {
        try {
            connection.send(false);
        } finally {
            connection.close();
        }
    }

    private void success(NodeConnection connection) throws IOException {
        connection.send(true);
    }

    @Override
    public void onExit() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error(e);
        }
        logger.debug(this.getClass().getSimpleName() + " is done");
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }
}