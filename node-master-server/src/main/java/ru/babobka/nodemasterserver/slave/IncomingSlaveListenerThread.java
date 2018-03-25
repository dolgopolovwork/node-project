package ru.babobka.nodemasterserver.slave;

import ru.babobka.nodebusiness.service.AuthService;
import ru.babobka.nodebusiness.service.MasterAuthService;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
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
    private final AuthService authService = Container.getInstance().get(MasterAuthService.class);
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
    public void onAwake() {
        try {
            Socket socket = serverSocket.accept();
            NodeConnection connection = nodeConnectionFactory.create(socket);
            connection.setReadTimeOut(config.getAuthTimeOutMillis());
            logger.info("new connection");
            if (auth(connection)) {
                logger.info("new slave was successfully authenticated");
                Set<String> availableTasks = connection.receive();
                boolean containsAnyOfTask = taskPool.containsAnyOfTask(availableTasks);
                connection.send(containsAnyOfTask);
                if (!containsAnyOfTask) {
                    logger.error("new slave doesn't have any common tasks with master");
                    return;
                }
                Slave slave = slaveFactory.create(availableTasks, connection);
                slavesStorage.add(slave);
                slave.start();
                connection.setReadTimeOut(config.getRequestTimeOutMillis());
            } else {
                logger.warning("auth fail");
                connection.close();
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed() || !Thread.currentThread().isInterrupted()) {
                logger.error(e);
            }
        }
    }

    boolean auth(NodeConnection connection) {
        try {
            return authService.auth(connection);
        } catch (RuntimeException e) {
            logger.error("error occurred while authenticating new slave", e);
            return false;
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