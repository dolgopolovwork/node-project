package ru.babobka.nodetester.slave;

import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.thread.CyclicThread;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.util.List;
import java.util.Random;

/**
 * Created by 123 on 19.11.2017.
 */
public class GlitchThread extends CyclicThread {
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final List<SlaveServer> slaveServerList;
    private final String login;
    private final String password;

    public GlitchThread(String login, String password, List<SlaveServer> slaveServerList) {
        ArrayUtil.validateNonNull(login, password, slaveServerList);
        this.login = login;
        this.password = password;
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
            nodeLogger.debug("removing slave");
            removeRandomSlave(slaveServerList);
            try {
                sleep(500);
                SlaveServer slaveServer = SlaveServerRunner.runSlaveServer(login, password);
                slaveServerList.add(slaveServer);
            } catch (Exception e) {
                nodeLogger.error(e);
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
