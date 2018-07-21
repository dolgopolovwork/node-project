package ru.babobka.nodetester.benchmark.performer;

import ru.babobka.nodemasterserver.server.config.PortConfig;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 123 on 29.07.2018.
 */
public abstract class CustomBenchmarkPerformer implements BenchmarkPerformer {
    @Override
    public boolean onBenchmark(PortConfig portConfig, int tests, AtomicLong timer) {
        try {
            for (int test = 0; test < tests; test++) {
                performBenchmark(portConfig, timer);
                BenchmarkPerformer.printTest(test);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected abstract void performBenchmark(PortConfig portConfig, AtomicLong timer) throws InterruptedException;
}
