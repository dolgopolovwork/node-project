package ru.babobka.nodemasterserver.slave;

import ru.babobka.nodemasterserver.server.MasterServerConfig;
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
    private final TaskPool taskPool = Container.getInstance().get("masterServerTaskPool");

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
            connection.setReadTimeOut(config.getAuthTimeOutMillis());
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
            connection.send(containsAnyOfTask);
            if (!containsAnyOfTask) {
                logger.error("new slave doesn't have any common tasks with master");
                return;
            }
            Slave slave = slaveFactory.create(availableTasks, new SecureNodeConnection(connection, authResult.getSecretKey()));
            slavesStorage.add(slave);
            slave.start();
            connection.setReadTimeOut(config.getRequestTimeOutMillis());
        } catch (IOException e) {
            if (!serverSocket.isClosed() || !Thread.currentThread().isInterrupted()) {
                logger.error(e);
            }
            if (connection != null) {
                connection.close();
            }
        }
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