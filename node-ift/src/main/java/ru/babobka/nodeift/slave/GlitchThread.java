package ru.babobka.nodeift.slave;

import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.thread.CyclicThread;

import java.util.List;
import java.util.Random;

/**
 * Created by 123 on 19.11.2017.
 */
public class GlitchThread extends CyclicThread {
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final List<SlaveServer> slaveServerList;
    private final String login;
    private final String password;

    public GlitchThread(String login, String password, List<SlaveServer> slaveServerList) {
        if (login == null) {
            throw new IllegalArgumentException("login is null");
        } else if (password == null) {
            throw new IllegalArgumentException("password is null");
        } else if (slaveServerList == null) {
            throw new IllegalArgumentException("slaveServerList is null");
        }
        this.login = login;
        this.password = password;
        this.slaveServerList = slaveServerList;
    }

    @Override
    public void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            interrupt();
            logger.error(e);
        }
    }

    @Override
    public synchronized void onAwake() {
        Random random = new Random();
        if (slaveServerList.isEmpty()) {
            return;
        }
        int randomSlave = random.nextInt(slaveServerList.size());
        slaveServerList.get(randomSlave).interrupt();
        slaveServerList.remove(randomSlave);
        try {
            Thread.sleep(500);
            slaveServerList.add(SlaveServerRunner.runSlaveServer(login, password));
        } catch (InterruptedException e) {
            interrupt();
        } catch (Exception e) {
            logger.error(e);
        }
    }

}
