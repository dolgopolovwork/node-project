package ru.babobka.nodemasterserver.slave;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.slave.pipeline.PipeContext;
import ru.babobka.nodemasterserver.slave.pipeline.SlaveCreatingPipelineFactory;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Pipeline;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeutils.thread.CyclicThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class IncomingSlaveListenerThread extends CyclicThread {
    private static final AtomicInteger INCOMING_SLAVE_ID = new AtomicInteger();
    private static final Logger logger = Logger.getLogger(IncomingSlaveListenerThread.class);
    private final ServerSocket serverSocket;
    private final NodeConnectionFactory nodeConnectionFactory = Container.getInstance().get(NodeConnectionFactory.class);
    private final MasterServerConfig config = Container.getInstance().get(MasterServerConfig.class);
    private final SlaveCreatingPipelineFactory slaveCreatingPipelineFactory = Container.getInstance().get(SlaveCreatingPipelineFactory.class);

    public IncomingSlaveListenerThread(@NonNull ServerSocket serverSocket) {
        if (serverSocket.isClosed()) {
            throw new IllegalArgumentException("serverSocket is closed");
        }
        this.serverSocket = serverSocket;
        setName("incoming_slave_listener_" + INCOMING_SLAVE_ID.getAndIncrement());
    }

    @Override
    public void onCycle() {
        NodeConnection connection = null;
        try {
            Socket socket = serverSocket.accept();
            connection = nodeConnectionFactory.create(socket);
            logger.info("new connection");
            PipeContext pipeContext = new PipeContext(connection);
            Pipeline<PipeContext> slaveCreatingPipeLine = slaveCreatingPipelineFactory.create(pipeContext);
            if (slaveCreatingPipeLine.execute(pipeContext)) {
                connection.setReadTimeOut(config.getTime().getRequestReadTimeOutMillis());
            } else {
                logger.error("cannot run slave due to pipeline failure");
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed() || !Thread.currentThread().isInterrupted()) {
                logger.error("exception thrown", e);
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
            logger.error("exception thrown", e);
        }
        logger.debug(this.getClass().getSimpleName() + " is done");
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("exception thrown", e);
        }
    }
}
