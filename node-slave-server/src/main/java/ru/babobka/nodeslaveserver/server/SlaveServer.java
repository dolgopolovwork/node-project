package ru.babobka.nodeslaveserver.server;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodesecurity.auth.AuthCredentials;
import ru.babobka.nodesecurity.network.ClientSecureNodeConnection;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeslaveserver.controller.SocketController;
import ru.babobka.nodeslaveserver.exception.SlaveAuthException;
import ru.babobka.nodeslaveserver.exception.SlaveStartupException;
import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodeslaveserver.server.pipeline.SlavePipelineFactory;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Pipeline;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;

public class SlaveServer extends Thread {

    private static final Logger logger = Logger.getLogger(SlaveServer.class);
    private final NodeConnectionFactory nodeConnectionFactory = Container.getInstance().get(NodeConnectionFactory.class);
    private final SlavePipelineFactory slavePipelineFactory = Container.getInstance().get(SlavePipelineFactory.class);
    private final NodeConnection connection;
    private final TasksStorage tasksStorage;

    public SlaveServer(@NonNull Socket socket,
                       @NonNull String login,
                       @NonNull String password) throws IOException {

        NodeConnection connection = nodeConnectionFactory.create(socket);
        AuthCredentials credentials = new AuthCredentials(login, password);
        PipeContext pipeContext = new PipeContext(connection, credentials);
        Pipeline<PipeContext> slaveCreationPipeline = slavePipelineFactory.create(pipeContext);
        if (!slaveCreationPipeline.execute(pipeContext)) {
            if (pipeContext.getAuthResult() != null && !pipeContext.getAuthResult().isSuccess()) {
                throw new SlaveAuthException("slave authentication error");
            }
            throw new SlaveStartupException("cannot start slave due to pipeline failure");
        }
        this.connection = createClientConnection(connection, pipeContext);
        tasksStorage = new TasksStorage();
        setName("slave server thread");
    }

    static ClientSecureNodeConnection createClientConnection(NodeConnection connection, PipeContext pipeContext) {
        return new ClientSecureNodeConnection(
                pipeContext.getServerTime(), connection,
                pipeContext.getAuthResult().getSecretKey());
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
            logger.error("exception thrown", e);
        } finally {
            logger.info("exiting slave server");
            clear();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            connection.send(NodeResponse.death());
        } catch (IOException e) {
            //TODO this shit produces StackOverflowError
            logger.warn("cannot send death message", e);
        }
        clear();
    }


    NodeConnection getConnection() {
        return connection;
    }

    void clear() {
        tasksStorage.stopAllTheTasks();
        connection.close();
    }

}
