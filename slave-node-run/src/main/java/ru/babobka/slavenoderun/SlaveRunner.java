package ru.babobka.slavenoderun;

import org.apache.log4j.Logger;
import ru.babobka.nodeslaveserver.exception.SlaveAuthException;
import ru.babobka.nodeutils.key.SlaveServerKey;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.waiter.Waiter;
import ru.babobka.slavenoderun.factory.SlaveServerRunnerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 123 on 10.12.2017.
 */
class SlaveRunner {

    private static final long RECONNECTION_TIMEOUT_MILLIS = 1500;
    private static final Logger logger = Logger.getLogger(SlaveRunner.class);
    private final Waiter slaveCreationWaiter = Container.getInstance().get(SlaveServerKey.SLAVE_CREATION_WAITER);
    private final SlaveServerRunnerFactory slaveServerFactory =
            Container.getInstance().get(SlaveServerRunnerFactory.class);
    private final AtomicBoolean started = new AtomicBoolean(false);

    void run() {
        checkWasStarted();
        while (!Thread.currentThread().isInterrupted()) {
            SlaveServer slaveServer = createSlaveWhileNotConnected();
            if (slaveServer == null) {
                return;
            }
            slaveServer.start();
            try {
                slaveServer.join();
                waitReconnection();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private SlaveServer createSlaveWhileNotConnected() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                slaveCreationWaiter.waitUntilAble();
                return slaveServerFactory.build();
            } catch (SlaveAuthException e) {
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("wait slave reconnection");
                waitReconnection();
            } catch (GeneralSecurityException e) {
                logger.error("error occurred while creating slave", e);
                return null;
            } catch (Exception e) {
                logger.error("cannot create slave due to severe error", e);
                return null;
            }
        }
        return null;
    }

    private static void waitReconnection() {
        try {
            Thread.sleep(RECONNECTION_TIMEOUT_MILLIS);
        } catch (InterruptedException ignored) {

        }
    }

    private void checkWasStarted() {
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("Cannot run slave runner twice");
        }
    }
}
