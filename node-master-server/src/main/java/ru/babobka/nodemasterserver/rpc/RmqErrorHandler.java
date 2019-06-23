package ru.babobka.nodemasterserver.rpc;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.impl.DefaultExceptionHandler;
import org.apache.log4j.Logger;

public class RmqErrorHandler extends DefaultExceptionHandler {
    private static final Logger logger = Logger.getLogger(RmqErrorHandler.class);

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
            value = "DM_EXIT",
            justification = "The server must die in case of RMQ failure because it cannot make any progress")
    @Override
    public void handleConnectionRecoveryException(Connection connection, Throwable exception) {
        logger.error("rmq exception. exit.", exception);
        System.exit(1);
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
            value = "DM_EXIT",
            justification = "The server must die in case of RMQ failure because it cannot make any progress")
    @Override
    public void handleUnexpectedConnectionDriverException(Connection connection, Throwable exception) {
        logger.error("rmq exception. exit.", exception);
        System.exit(1);
    }

}
