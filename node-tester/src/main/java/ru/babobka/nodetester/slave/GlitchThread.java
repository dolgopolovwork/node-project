package ru.babobka.nodetester.slave;

import org.apache.log4j.Logger;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeutils.thread.CyclicThread;
import ru.babobka.nodeutils.util.TextUtil;

import java.util.List;
import java.util.Random;

/**
 * Created by 123 on 19.11.2017.
 */
public class GlitchThread extends CyclicThread {
    private static final Logger logger = Logger.getLogger(GlitchThread.class);
    private final List<SlaveServer> slaveServerList;
    private final String login;

    public GlitchThread(String login, List<SlaveServer> slaveServerList) {
        if (TextUtil.isEmpty(login)) {
            throw new IllegalArgumentException("login was not set");
        } else if (slaveServerList == null) {
            throw new IllegalArgumentException("slaveServerList was not set");
        }
        this.login = login;
        this.slaveServerList = slaveServerList;
    }

    @Override
    public int sleepMillis() {
        return 1000;
    }

    @Override
    public synchronized void onCycle() {
        synchronized (slaveServerList) {
            if (slaveServerList.isEmpty()) {
                return;
            }
            logger.debug("removing slave");
            removeRandomSlave(slaveServerList);
            try {
                sleep(500);
                SlaveServer slaveServer = SlaveServerRunner.runSlaveServer(login);
                slaveServerList.add(slaveServer);
            } catch (Exception e) {
                logger.error("exception thrown", e);
            }
        }
    }

    void removeRandomSlave(List<SlaveServer> slaveServerList) {
        Random random = new Random();
        int randomSlave = random.nextInt(slaveServerList.size());
        slaveServerList.get(randomSlave).interrupt();
        slaveServerList.remove(randomSlave);
    }

}
