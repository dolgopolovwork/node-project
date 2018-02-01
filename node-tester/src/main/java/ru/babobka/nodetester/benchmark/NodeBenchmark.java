package ru.babobka.nodetester.benchmark;

import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;

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

    SlaveServerCluster createCluster(String login, String password, int slaves) throws IOException {
        return new SlaveServerCluster(login, password, slaves);
    }

}
