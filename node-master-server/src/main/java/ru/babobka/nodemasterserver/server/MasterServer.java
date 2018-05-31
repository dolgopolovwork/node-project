package ru.babobka.nodemasterserver.server;

import ru.babobka.nodebusiness.dao.CacheDAO;
import ru.babobka.nodebusiness.service.NodeUsersService;
import ru.babobka.nodemasterserver.client.ClientStorage;
import ru.babobka.nodemasterserver.client.IncomingClientListenerThread;
import ru.babobka.nodemasterserver.monitoring.TaskMonitoringService;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.slave.IncomingSlaveListenerThread;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.thread.HeartBeatingThread;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.vsjws.webserver.WebServer;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;


/**
 * Created by dolgopolov.a on 16.07.15.
 */
public class MasterServer extends Thread {

    private final Thread heartBeatingThread = Container.getInstance().get(HeartBeatingThread.class);
    private final Thread incomingClientsThread = Container.getInstance().get(IncomingClientListenerThread.class);
    private final Thread incomingSlavesThread = Container.getInstance().get(IncomingSlaveListenerThread.class);
    private final WebServer webServer = Container.getInstance().get(WebServer.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final ClientStorage clientStorage = Container.getInstance().get(ClientStorage.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
    private final NodeUsersService nodeUsersService = Container.getInstance().get(NodeUsersService.class);

    @Override
    public void run() {
        try {
            if (masterServerConfig.getModes().isTestUserMode()) {
                nodeUsersService.createDebugUser();
            }
            incomingClientsThread.start();
            incomingSlavesThread.start();
            heartBeatingThread.start();
            webServer.start();
        } catch (RuntimeException e) {
            nodeLogger.error(e);
            clear();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        slavesStorage.closeStorage();
        clientStorage.closeStorage();
        interruptAndJoin(incomingClientsThread);
        interruptAndJoin(incomingSlavesThread);
        interruptAndJoin(webServer);
        interruptAndJoin(heartBeatingThread);
        clear();
    }

    synchronized void clear() {
        try {
            Container.getInstance().get(CacheDAO.class).close();
        } catch (IOException e) {
            nodeLogger.error("can not close cache", e);
        }
        if (slavesStorage != null) {
            slavesStorage.clear();
        }
        if (clientStorage != null) {
            clientStorage.clear();
        }
    }

    void interruptAndJoin(Thread thread) {
        thread.interrupt();
        try {
            thread.join(10_000);
        } catch (InterruptedException e) {
            nodeLogger.error(e);
        }
    }


    public static void runMBeanServer() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            mBeanServer.registerMBean(Container.getInstance().get(TaskMonitoringService.class), new ObjectName("node-project:type=benchmark,name=task monitoring"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}