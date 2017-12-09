package ru.babobka.nodemasterserver.thread;

import ru.babobka.nodemasterserver.client.ClientStorage;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.thread.CyclicThread;

public class HeartBeatingThread extends CyclicThread {

    private final MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final ClientStorage clientStorage = Container.getInstance().get(ClientStorage.class);
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    @Override
    public void sleep() throws InterruptedException {
        Thread.sleep(masterServerConfig.getHeartBeatTimeOutMillis());
    }

    @Override
    public void onAwake() {
        logger.debug("Heart beating time");
        clientStorage.heartBeatAllClients();
        slavesStorage.heartBeatAllSlaves();
    }

}
