package ru.babobka.nodeslaveserver.controller;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;

import static ru.babobka.nodeutils.util.StreamUtil.isClosedConnectionException;

public abstract class AbstractSocketController implements Closeable {

    private final SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
    private static final Logger logger = Logger.getLogger(AbstractSocketController.class);
    protected final NodeConnection connection;

    AbstractSocketController(@NonNull NodeConnection connection) {
        this.connection = connection;
    }

    public void control() {
        try {
            doControl(connection);
        } catch (Exception e) {
            if (!connection.isClosed() && !isClosedConnectionException(e)) {
                throw new IllegalStateException("cannot control", e);
            }
        }
    }

    public abstract void onStop(NodeRequest request);

    public abstract void onExecute(NodeRequest request);

    private void doControl(NodeConnection connection) throws IOException {
        connection.setReadTimeOut(slaveServerConfig.getRequestTimeoutMillis());
        NodeRequest request = connection.receive();
        if (request.getRequestStatus() == RequestStatus.HEART_BEAT) {
            connection.send(NodeResponse.heartBeat());
        } else if (request.getRequestStatus() == RequestStatus.STOP) {
            logger.info("stopping request " + request);
            onStop(request);
        } else {
            logger.info("executing request " + request);
            onExecute(request);
        }
    }
}
