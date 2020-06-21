package ru.babobka.nodetester.benchmark;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodebusiness.debug.DebugBase64KeyPair;
import ru.babobka.nodebusiness.debug.DebugCredentials;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.master.PortConfig;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodetester.benchmark.performer.BenchmarkPerformer;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.key.UtilKey;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 123 on 30.01.2018.
 */
public class NodeBenchmark {

    private static final Logger logger = Logger.getLogger(NodeBenchmark.class);
    private static final String LOGIN = DebugCredentials.USER_NAME;
    private final String appName;
    private final int tests;

    public NodeBenchmark(@NonNull String appName, int tests) {
        this.tests = tests;
        this.appName = appName;
    }

    BenchmarkData executeCycledBenchmark(PortConfig portConfig, int tests, BenchmarkPerformer performer) {
        AtomicLong timer = new AtomicLong();
        if (!performer.onBenchmark(portConfig, tests, timer)) {
            return null;
        }
        long time = (long) (timer.get() / (double) tests);
        return new BenchmarkData(appName, time);
    }

    public synchronized void run(int slaves, int serviceThreads, BenchmarkPerformer benchmarkPerformer) {
        if (slaves < 0 || serviceThreads < 0) {
            throw new IllegalArgumentException("Both slaves and serviceThreads must be at least 1");
        }
        try {
            MasterServerRunner.init();
            MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
            PublicKey publicKey = KeyDecoder.decodePublicKey(masterServerConfig.getKeyPair().getPubKey());
            SlaveServerRunner.init(publicKey);
            startMonitoring();
            Container.getInstance().put(UtilKey.SERVICE_THREADS_NUM, serviceThreads);
            MasterServer masterServer = MasterServerRunner.runMasterServer();
            try (SlaveServerCluster slaveServerCluster = createCluster(
                    LOGIN, slaves)) {
                slaveServerCluster.start();
                BenchmarkData benchmarkData = executeCycledBenchmark(masterServerConfig.getPorts(), tests, benchmarkPerformer);
                if (benchmarkData != null) {
                    CLI.print(benchmarkData.toString());
                }
            } finally {
                masterServer.interrupt();
            }
        } catch (Exception e) {
            logger.error("exception thrown", e);
        }
    }

    void startMonitoring() {
        MasterServer.runMBeanServer();
    }

    SlaveServerCluster createCluster(String login, int slaves) throws IOException {
        return new SlaveServerCluster(login, slaves);
    }

}
