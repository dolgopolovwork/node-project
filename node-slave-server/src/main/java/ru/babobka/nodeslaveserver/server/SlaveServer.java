package ru.babobka.nodeslaveserver.server;

import io.javalin.Javalin;
import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodesecurity.auth.AuthCredentials;
import ru.babobka.nodesecurity.network.ClientSecureNodeConnection;
import ru.babobka.nodesecurity.sign.Signer;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeslaveserver.controller.AbstractSocketController;
import ru.babobka.nodeslaveserver.controller.ControllerFactory;
import ru.babobka.nodeslaveserver.exception.SlaveAuthException;
import ru.babobka.nodeslaveserver.exception.SlaveStartupException;
import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodeslaveserver.server.pipeline.SlavePipelineFactory;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Pipeline;
import ru.babobka.nodeutils.key.MasterServerKey;
import ru.babobka.nodeutils.key.SlaveServerKey;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.babobka.nodeutils.util.StreamUtil.isClosedConnectionException;

public class SlaveServer extends Thread {

    private static final AtomicInteger SLAVE_ID = new AtomicInteger();
    private static final Logger logger = Logger.getLogger(SlaveServer.class);
    private final Javalin webServer = Container.getInstance().get(SlaveServerKey.SLAVE_WEB);
    private final NodeConnectionFactory nodeConnectionFactory = Container.getInstance().get(NodeConnectionFactory.class);
    private final SlavePipelineFactory slavePipelineFactory = Container.getInstance().get(SlavePipelineFactory.class);
    private final Signer signer = Container.getInstance().get(SlaveServerKey.SLAVE_DSA_MANAGER);
    private final NodeConnection connection;
    private final TasksStorage tasksStorage;
    private final ControllerFactory controllerFactory;
    private final int webPort;

    SlaveServer(@NonNull Socket socket,
                @NonNull String login,
                @NonNull ControllerFactory controllerFactory,
                int webPort) throws IOException {
        NodeConnection connection = nodeConnectionFactory.create(socket);
        AuthCredentials credentials = new AuthCredentials(login);
        PipeContext pipeContext = new PipeContext(connection, credentials);
        Pipeline<PipeContext> slaveCreationPipeline = slavePipelineFactory.create(pipeContext);
        if (!slaveCreationPipeline.execute(pipeContext)) {
            if (pipeContext.getAuthResult() != null && !pipeContext.getAuthResult().isSuccess()) {
                throw new SlaveAuthException("slave authentication error");
            }
            throw new SlaveStartupException("cannot start slave due to pipeline failure");
        }
        this.connection = createClientConnection(signer, connection, pipeContext);
        this.controllerFactory = controllerFactory;
        tasksStorage = new TasksStorage();
        setName("slave_server_" + SLAVE_ID.getAndIncrement());
        this.webPort = webPort;
    }

    static ClientSecureNodeConnection createClientConnection(Signer signer, NodeConnection connection, PipeContext pipeContext) {
        return new ClientSecureNodeConnection(
                signer,
                pipeContext.getServerTime(), connection,
                pipeContext.getAuthResult().getPublicKey());
    }

    @Override
    public void run() {
        webServer.start(this.webPort);
        try (AbstractSocketController controller = controllerFactory.create(connection, tasksStorage)) {
            while (!isInterrupted() && !connection.isClosed()) {
                controller.control();
            }
        } catch (IOException | RuntimeException e) {
            if (!connection.isClosed() && !isClosedConnectionException(e)) {
                logger.error("exception thrown", e);
            }
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
            logger.warn("cannot send death message", e);
        }
        webServer.stop();
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
