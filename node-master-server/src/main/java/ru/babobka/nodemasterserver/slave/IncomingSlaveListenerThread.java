package ru.babobka.nodemasterserver.slave;

import lombok.NonNull;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.slave.pipeline.PipeContext;
import ru.babobka.nodemasterserver.slave.pipeline.SlaveCreatingPipelineFactory;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Pipeline;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeutils.thread.CyclicThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class IncomingSlaveListenerThread extends CyclicThread {

    private final ServerSocket serverSocket;
    private final NodeConnectionFactory nodeConnectionFactory = Container.getInstance().get(NodeConnectionFactory.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final MasterServerConfig config = Container.getInstance().get(MasterServerConfig.class);
    private final SlaveCreatingPipelineFactory slaveCreatingPipelineFactory = Container.getInstance().get(SlaveCreatingPipelineFactory.class);

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
            nodeLogger.info("new connection");
            PipeContext pipeContext = new PipeContext(connection);
            Pipeline<PipeContext> slaveCreatingPipeLine = slaveCreatingPipelineFactory.create(pipeContext);
            if (slaveCreatingPipeLine.execute(pipeContext)) {
                connection.setReadTimeOut(config.getTime().getRequestReadTimeOutMillis());
            } else {
                nodeLogger.error("cannot run slave due to pipeline failure");
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed() || !Thread.currentThread().isInterrupted()) {
                nodeLogger.error(e);
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