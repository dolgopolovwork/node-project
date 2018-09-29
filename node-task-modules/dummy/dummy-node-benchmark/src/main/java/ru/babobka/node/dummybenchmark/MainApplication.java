package ru.babobka.node.dummybenchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodetester.benchmark.NodeBenchmarkCLI;

public class MainApplication extends NodeBenchmarkCLI {

    @Override
    protected Options createBenchmarkOptions() {
        return new Options();
    }

    @Override
    protected void benchMarkRun(CommandLine cmd) {
        new NodeBenchmark(getAppName(), getTests(cmd)).run(getSlaves(cmd), getServiceThreads(cmd), new DummyNodeBenchmarkPerformer());
    }

    @Override
    public String getAppName() {
        return "dummy-node-benchmark";
    }

    public static void main(String[] args) {
        new MainApplication().onMain(args);
    }
}
