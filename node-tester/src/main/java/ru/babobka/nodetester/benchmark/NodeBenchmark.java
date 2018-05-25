package ru.babobka.nodetester.benchmark;

import ru.babobka.nodebusiness.model.Benchmark;
import ru.babobka.nodebusiness.service.BenchmarkStorageService;
import ru.babobka.nodeclient.CLI;
import ru.babobka.nodeclient.Client;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodetester.benchmark.mapper.BenchmarkMapper;
import ru.babobka.nodetester.key.TesterKey;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.key.UtilKey;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by 123 on 30.01.2018.
 */
public abstract class NodeBenchmark {

    private static final String LOGIN = "test_user";
    private static final String PASSWORD = "test_password";
    private final BenchmarkMapper benchmarkMapper = new BenchmarkMapper();
    private final long startTime;
    private final String appName;
    private final int tests;

    protected NodeBenchmark(String appName, int tests) {
        if (appName == null) {
            throw new IllegalArgumentException("appName is null");
        }
        this.tests = tests;
        this.appName = appName;
        this.startTime = System.currentTimeMillis();
    }

    BenchmarkData executeCycledBenchmark(int tests) {
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        int port = masterServerConfig.getPorts().getClientListenerPort();
        AtomicLong timer = new AtomicLong();
        try (Client client = createLocalClient(port)) {
            for (int test = 0; test < tests; test++) {
                onBenchmark(client, timer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        long time = (long) (timer.get() / (double) tests);
        return new BenchmarkData(getDescription(), time);
    }

    protected abstract String getDescription();

    protected abstract void onBenchmark(Client client, AtomicLong timerStorage) throws IOException, ExecutionException, InterruptedException;

    public synchronized void run(int slaves, int serviceThreads) {
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
            BenchmarkData benchmarkData = executeCycledBenchmark(tests);
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

    Client createLocalClient(int port) {
        return new Client("localhost", port);
    }

    SlaveServerCluster createCluster(String login, String password, int slaves) throws IOException {
        return new SlaveServerCluster(login, password, slaves);
    }

}
