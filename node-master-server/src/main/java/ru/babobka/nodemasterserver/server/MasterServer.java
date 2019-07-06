package ru.babobka.nodemasterserver.server;

import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;
import ru.babobka.nodebusiness.dao.CacheDAO;
import ru.babobka.nodebusiness.monitoring.TaskMonitoringService;
import ru.babobka.nodebusiness.service.NodeUsersService;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.client.ClientStorage;
import ru.babobka.nodemasterserver.client.IncomingClientListenerThread;
import ru.babobka.nodemasterserver.rpc.RpcServer;
import ru.babobka.nodemasterserver.slave.IncomingSlaveListenerThread;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.thread.HeartBeatingThread;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.TextUtil;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by dolgopolov.a on 16.07.15.
 */
public class MasterServer extends Thread {

    private static final AtomicInteger MASTER_SERVER_ID = new AtomicInteger();
    private final Thread heartBeatingThread = Container.getInstance().get(HeartBeatingThread.class);
    private final Thread incomingClientsThread = Container.getInstance().get(IncomingClientListenerThread.class);
    private final Thread incomingSlavesThread = Container.getInstance().get(IncomingSlaveListenerThread.class);
    private final HttpServer webServer = Container.getInstance().get(HttpServer.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final ClientStorage clientStorage = Container.getInstance().get(ClientStorage.class);
    private static final Logger logger = Logger.getLogger(MasterServer.class);
    private final MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
    private final NodeUsersService nodeUsersService = Container.getInstance().get(NodeUsersService.class);
    private final RpcServer rpcServer;

    public MasterServer() throws IOException {
        setName("master_server_" + MASTER_SERVER_ID.getAndIncrement());
        if (masterServerConfig.getRmq() != null) {
            rpcServer = new RpcServer();
        } else {
            rpcServer = null;
        }
    }

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
            if (rpcServer != null) {
                rpcServer.start();
            }
            logger.info(TextUtil.WELCOME_TEXT);
        } catch (Exception e) {
            logger.error("exception thrown", e);
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
        interruptAndJoin(heartBeatingThread);
        if (rpcServer != null) {
            rpcServer.close();
        }
        webServer.stop(0);
        clear();
    }

    synchronized void clear() {
        try {
            Container.getInstance().get(CacheDAO.class).close();
        } catch (IOException e) {
            logger.error("can not close cache", e);
        }
        if (slavesStorage != null) {
            slavesStorage.clear();
        }
        if (clientStorage != null) {
            clientStorage.clear();
        }
    }

    private void interruptAndJoin(Thread thread) {
        thread.interrupt();
        try {
            thread.join(10_000);
        } catch (InterruptedException e) {
            logger.error("exception thrown", e);
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
