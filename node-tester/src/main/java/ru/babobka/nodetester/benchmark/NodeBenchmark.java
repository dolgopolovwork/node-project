package ru.babobka.nodetester.benchmark;

import ru.babobka.nodeclient.Client;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.service.TaskMonitoringService;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * Created by 123 on 30.01.2018.
 */
public abstract class NodeBenchmark {

    private static final String LOGIN = "test_user";
    private static final String PASSWORD = "test_password";

    protected abstract void onBenchmark();

    public synchronized void run(int slaves, int serviceThreads) {
        if (slaves < 0 || serviceThreads < 0) {
            throw new IllegalArgumentException("Both slaves and serviceThreads must be at least 1");
        }
        MasterServerRunner.init();
        SlaveServerRunner.init();
        runMBeanServer();
        Container.getInstance().put("service-threads", serviceThreads);
        MasterServer masterServer = MasterServerRunner.runMasterServer();
        try (SlaveServerCluster slaveServerCluster = createCluster(LOGIN, PASSWORD, slaves)) {
            slaveServerCluster.start();
            onBenchmark();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            masterServer.interrupt();
        }
    }

    protected Client createClient(String host, int port) {
        return new Client(host, port);
    }

    void runMBeanServer() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            mBeanServer.registerMBean(Container.getInstance().get(TaskMonitoringService.class), new ObjectName("node-project:type=benchmark,name=task monitoring"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    SlaveServerCluster createCluster(String login, String password, int slaves) throws IOException {
        return new SlaveServerCluster(login, password, slaves);
    }

}
