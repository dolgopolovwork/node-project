package ru.babobka.nodetester.benchmark;

import org.json.JSONObject;
import ru.babobka.nodebusiness.model.Benchmark;
import ru.babobka.nodebusiness.service.BenchmarkStorageService;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.server.config.PortConfig;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodetester.benchmark.mapper.BenchmarkMapper;
import ru.babobka.nodetester.benchmark.performer.BenchmarkPerformer;
import ru.babobka.nodetester.key.TesterKey;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.key.UtilKey;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by 123 on 30.01.2018.
 */
public class NodeBenchmark {

    private static final String LOGIN = "test_user";
    private static final String PASSWORD = "test_password";
    private final BenchmarkMapper benchmarkMapper = new BenchmarkMapper();
    private final long startTime;
    private final String appName;
    private final int tests;

    public NodeBenchmark(String appName, int tests) {
        if (appName == null) {
            throw new IllegalArgumentException("appName is null");
        }
        this.tests = tests;
        this.appName = appName;
        this.startTime = System.currentTimeMillis();
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
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        RSAPublicKey publicKey = masterServerConfig.getSecurity().getRsaConfig().getPublicKey();
        SlaveServerRunner.init(publicKey);
        startMonitoring();
        Container.getInstance().put(UtilKey.SERVICE_THREADS_NUM, serviceThreads);
        MasterServer masterServer = MasterServerRunner.runMasterServer();
        try (SlaveServerCluster slaveServerCluster = createCluster(LOGIN, PASSWORD, slaves)) {
            slaveServerCluster.start();
            BenchmarkData benchmarkData = executeCycledBenchmark(masterServerConfig.getPorts(), tests, benchmarkPerformer);
            if (benchmarkData != null) {
                CLI.print(benchmarkData.toString());
                saveBenchmark(benchmarkData, slaves, serviceThreads);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            masterServer.interrupt();
        }
    }

    protected long getStartTime() {
        return startTime;
    }

    void startMonitoring() {
        MasterServer.runMBeanServer();
    }

    private void saveBenchmark(BenchmarkData benchmarkData, int slaves, int serviceThreads) {
        boolean ableToSave = Properties.getBool(TesterKey.PERMANENT, false);
        if (!ableToSave) {
            return;
        }
        BenchmarkStorageService storageService = Container.getInstance().get(BenchmarkStorageService.class);
        Benchmark benchmark = benchmarkMapper.map(benchmarkData, getStartTime(), appName, slaves, serviceThreads);
        storageService.insert(benchmark);
    }

    SlaveServerCluster createCluster(String login, String password, int slaves) throws IOException {
        return new SlaveServerCluster(login, password, slaves);
    }

}
