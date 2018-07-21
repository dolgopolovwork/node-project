package ru.babobka.nodetester.benchmark.performer;

import ru.babobka.nodeclient.Client;
import ru.babobka.nodemasterserver.server.config.PortConfig;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 123 on 29.07.2018.
 */
public abstract class ClientBenchmarkPerformer implements BenchmarkPerformer {
    @Override
    public boolean onBenchmark(PortConfig portConfig, int tests, AtomicLong timer) {
        try (Client client = createLocalClient(portConfig.getClientListenerPort())) {
            for (int test = 0; test < tests; test++) {
                performBenchmark(client, timer);
                BenchmarkPerformer.printTest(test);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected abstract void performBenchmark(Client client, AtomicLong timer) throws IOException, ExecutionException, InterruptedException;

    private Client createLocalClient(int port) {
        return new Client("localhost", port);
    }
}
