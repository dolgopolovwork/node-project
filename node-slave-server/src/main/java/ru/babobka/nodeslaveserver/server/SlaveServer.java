package ru.babobka.nodeslaveserver.server;

import lombok.NonNull;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.network.SecureNodeConnection;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeslaveserver.controller.SocketController;
import ru.babobka.nodeslaveserver.exception.AuthFailException;
import ru.babobka.nodeslaveserver.key.SlaveServerKey;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;

public class SlaveServer extends Thread {

    private final SlaveAuthService authService = Container.getInstance().get(SlaveAuthService.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final TaskPool taskPool = Container.getInstance().get(SlaveServerKey.SLAVE_SERVER_TASK_POOL);
    private final NodeConnectionFactory nodeConnectionFactory = Container.getInstance().get(NodeConnectionFactory.class);
    private final NodeConnection connection;
    private final TasksStorage tasksStorage;

    public SlaveServer(@NonNull Socket socket,
                       @NonNull String login,
                       @NonNull String password) throws IOException {
        NodeConnection connection = nodeConnectionFactory.create(socket);
        AuthResult authResult = authService.authClient(connection, login, password);
        if (!authResult.isSuccess()) {
            connection.close();
            throw new AuthFailException("authClient fail");
        }
        nodeLogger.info("authClient success");
        connection.send(taskPool.getTaskNames());
        boolean haveCommonTasks = connection.receive();
        if (!haveCommonTasks) {
            connection.close();
            throw new AuthFailException("no common tasks with master server");
        }
        boolean serverAuthSuccess = authService.authServer(connection);
        if (!serverAuthSuccess) {
            connection.close();
            throw new AuthFailException("server auth fail");
        }
        boolean sessionWasCreated = connection.receive();
        if (!sessionWasCreated) {
            connection.close();
            throw new AuthFailException("cannot create session");
        }
        tasksStorage = new TasksStorage();
        this.connection = new SecureNodeConnection(connection, authResult.getSecretKey());
        setName("slave server thread");
    }

    @Override
    public void run() {
        try (SocketController controller = new SocketController(
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
                    Thread thread = Executors.defaultThreadFactory().newThread(r);
                    thread.setName("socket controller thread pool");
                    thread.setDaemon(true);
                    return thread;
                }),
                tasksStorage)) {
            while (!isInterrupted() && !connection.isClosed()) {
                controller.control(connection);
            }
        } catch (IOException | RuntimeException e) {
            nodeLogger.error(e);
        } finally {
            nodeLogger.info("exiting slave server");
            clear();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            connection.send(NodeResponse.death());
        } catch (IOException e) {
            nodeLogger.warning("cannot send death message", e);
        }
        clear();
    }


    void clear() {
        tasksStorage.stopAllTheTasks();
        if (connection != null) {
            connection.close();
        }
    }

}
