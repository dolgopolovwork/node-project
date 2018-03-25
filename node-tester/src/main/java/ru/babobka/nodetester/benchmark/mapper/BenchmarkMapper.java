package ru.babobka.nodetester.benchmark.mapper;

import ru.babobka.nodebusiness.model.Benchmark;
import ru.babobka.nodetester.benchmark.BenchmarkData;
import ru.babobka.nodeutils.func.Mapper;

import java.util.UUID;

/**
 * Created by 123 on 25.03.2018.
 */
public class BenchmarkMapper extends Mapper<BenchmarkData, Benchmark> {

    public Benchmark map(BenchmarkData benchmarkData, long startTime, String appName, int slaves, int serviceThreads) {
        Benchmark benchmark = map(benchmarkData);
        benchmark.setStartTime(startTime);
        benchmark.setAppName(appName);
        benchmark.setSlaves(slaves);
        benchmark.setServiceThreads(serviceThreads);
        return benchmark;
    }

    @Override
    protected Benchmark mapImpl(BenchmarkData benchmarkData) {
        Benchmark benchmark = new Benchmark();
        benchmark.setId(UUID.randomUUID().toString());
        benchmark.setExecutionTime(benchmarkData.getExecutionTime());
        benchmark.setDescription(benchmarkData.getDescription());
        benchmark.setCurrentSystemInfo();
        return benchmark;
    }
}
