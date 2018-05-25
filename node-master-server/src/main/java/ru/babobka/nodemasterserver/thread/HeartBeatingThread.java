package ru.babobka.nodemasterserver.thread;

import ru.babobka.nodemasterserver.client.ClientStorage;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.thread.CyclicThread;

public class HeartBeatingThread extends CyclicThread {

    public HeartBeatingThread() {
        setDaemon(true);
    }

    private final MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final ClientStorage clientStorage = Container.getInstance().get(ClientStorage.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);

    @Override
    public int sleepMillis() {
        return masterServerConfig.getTimeouts().getHeartBeatTimeOutMillis();
    }

    @Override
    public void onCycle() {
        nodeLogger.debug("heart beating time");
        clientStorage.heartBeatAllClients();
        slavesStorage.heartBeatAllSlaves();
    }

}
