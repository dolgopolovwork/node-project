package ru.babobka.nodetester.benchmark.performer;


import ru.babobka.nodeconfigs.master.PortConfig;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 123 on 29.07.2018.
 */
public interface BenchmarkPerformer {
    boolean onBenchmark(PortConfig masterServerPortConfig, int tests, AtomicLong timer);

    static void printTest(int test) {
        System.out.println(new Date() + " " + (test + 1) + " id done");
    }

}
