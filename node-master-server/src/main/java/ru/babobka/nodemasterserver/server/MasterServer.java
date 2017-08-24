package ru.babobka.nodemasterserver.server;

import ru.babobka.nodebusiness.service.NodeUsersService;
import ru.babobka.nodemasterserver.slave.IncomingSlaveListenerThread;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.thread.HeartBeatingThread;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.vsjws.webserver.WebServer;


/**
 * Created by dolgopolov.a on 16.07.15.
 */
public class MasterServer extends Thread {

    private final Thread heartBeatingThread = Container.getInstance().get(HeartBeatingThread.class);
    private final Thread incomingClientsThread = Container.getInstance().get(IncomingClientsThread.class);
    private final Thread listenerThread = Container.getInstance().get(IncomingSlaveListenerThread.class);
    private final WebServer webServer = Container.getInstance().get(WebServer.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
    private NodeUsersService nodeUsersService = Container.getInstance().get(NodeUsersService.class);

    @Override
    public void run() {
        try {
            if (masterServerConfig.isDebugMode()) {
                nodeUsersService.createDebugUser();
            }
            incomingClientsThread.start();
            listenerThread.start();
            heartBeatingThread.start();
            webServer.start();
        } catch (RuntimeException e) {
            logger.error(e);
            clear();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        clear();
    }

    synchronized void clear() {
        interruptAndJoin(incomingClientsThread);
        interruptAndJoin(webServer);
        interruptAndJoin(listenerThread);
        interruptAndJoin(heartBeatingThread);
        if (slavesStorage != null) {
            slavesStorage.clear();
        }
    }

    void interruptAndJoin(Thread thread) {
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            thread.interrupt();
            logger.error(e);
        }
    }

}