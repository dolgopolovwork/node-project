package ru.babobka.nodemasterserver.server;

import ru.babobka.nodemasterserver.client.Client;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.StoppedTasks;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;

/**
 * Created by 123 on 28.10.2017.
 */
public class IncomingClientsThread extends Thread {

    private final MasterServerConfig config = Container.getInstance().get(MasterServerConfig.class);
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final ExecutorService executorService = Container.getInstance().get("clientsThreadPool");
    private final StoppedTasks stoppedTasks = Container.getInstance().get(StoppedTasks.class);
    private final TaskService taskService = Container.getInstance().get(TaskService.class);

    @Override
    public void run() {
        try (ServerSocket serverSocket = createServerSocket(config.getClientListenerPort())) {
            while (isDone(serverSocket)) {
                processConnection(serverSocket);
            }
        } catch (IOException e) {
            logger.error(e);
        } finally {
            executorService.shutdownNow();
        }
    }

    @Override
    public void interrupt() {
        executorService.shutdownNow();
        super.interrupt();
    }

    void processConnection(ServerSocket serverSocket) {
        if (serverSocket == null) {
            throw new IllegalArgumentException("serverSocket is null");
        } else if (serverSocket.isClosed()) {
            throw new IllegalStateException("serverSocket is closed");
        }
        try (NodeConnection nodeConnection = createNodeConnection(serverSocket)) {
            NodeRequest request = nodeConnection.receive();
            handleRequest(nodeConnection, request);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    void handleRequest(NodeConnection nodeConnection, NodeRequest request) {
        switch (request.getRequestStatus()) {
            case NORMAL:
            case RACE: {
                try {
                    executorService.submit(new Client(nodeConnection, request, stoppedTasks));
                } catch (RuntimeException e) {
                    logger.error("Error while handling request " + request, e);
                }
                return;
            }
            case STOP: {
                try {
                    stoppedTasks.add(request);
                    taskService.cancelTask(request.getTaskId());
                } catch (TaskExecutionException e) {
                    logger.error(e);
                }
                return;
            }
            default: {
                logger.warning("Can not handle request " + request);
            }
        }
    }

    ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    boolean isDone(ServerSocket serverSocket) {
        if (serverSocket == null) {
            throw new IllegalArgumentException("serverSocket is null");
        }
        return serverSocket.isClosed() || Thread.currentThread().isInterrupted();
    }

    NodeConnection createNodeConnection(ServerSocket serverSocket) throws IOException {
        return new NodeConnection(serverSocket.accept());
    }
}
