package ru.babobka.slavenoderun;

import ru.babobka.nodeslaveserver.exception.SlaveAuthFailException;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;

/**
 * Created by 123 on 10.12.2017.
 */
public class ConnectionSafeSlaveRunner {

    private static final long RECONNECTION_TIMEOUT_MILLIS = 1500;
    private final SlaveServerFactory slaveServerFactory = Container.getInstance().get(SlaveServerFactory.class);

    private static void waitReconnection() {
        try {
            Thread.sleep(RECONNECTION_TIMEOUT_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run(String pathToConfig, String login, String hashedPassword) {
        while (!Thread.currentThread().isInterrupted()) {
            SlaveServer slaveServer = createSlaveWhileNotConnected(pathToConfig, login, hashedPassword);
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

    private SlaveServer createSlaveWhileNotConnected(String pathToConfig, String login, String hashedPassword) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                return slaveServerFactory.create(pathToConfig, login, hashedPassword);
            } catch (SlaveAuthFailException e) {
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                waitReconnection();
            }
        }
        return null;
    }
}
