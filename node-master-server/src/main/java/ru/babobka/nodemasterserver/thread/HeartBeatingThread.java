package ru.babobka.nodemasterserver.thread;

import org.apache.log4j.Logger;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.client.ClientStorage;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.thread.CyclicThread;

public class HeartBeatingThread extends CyclicThread {

    public HeartBeatingThread() {
        setDaemon(true);
        setName("heart_beating");
    }

    private static final Logger logger = Logger.getLogger(HeartBeatingThread.class);
    private final MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final ClientStorage clientStorage = Container.getInstance().get(ClientStorage.class);

    @Override
    public int sleepMillis() {
        return masterServerConfig.getTime().getHeartBeatCycleMillis();
    }

    @Override
    public void onCycle() {
        logger.debug("heart beating time");
        clientStorage.heartBeatAllClients();
        slavesStorage.heartBeatAllSlaves();
    }
}
