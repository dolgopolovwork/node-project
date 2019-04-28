package ru.babobka.node.dummybenchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodetester.benchmark.NodeBenchmarkCLI;

import java.util.Collections;
import java.util.List;

public class DummyNodeBenchmarkAp extends NodeBenchmarkCLI {

    @Override
    protected List<Option> createBenchmarkOptions() {
        return Collections.emptyList();
    }

    @Override
    protected void benchMarkRun(CommandLine cmd) {
        new NodeBenchmark(getAppName(), getTests(cmd))
                .run(getSlaves(cmd), getServiceThreads(cmd), new DummyNodeBenchmarkPerformer());
    }

    @Override
    public String getAppName() {
        return "dummy-node-benchmark";
    }

    public static void main(String[] args) {
        new DummyNodeBenchmarkAp().onStart(args);
    }
}
