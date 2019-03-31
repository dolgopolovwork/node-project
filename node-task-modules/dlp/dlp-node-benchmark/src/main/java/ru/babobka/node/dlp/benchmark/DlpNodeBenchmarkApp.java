package ru.babobka.node.dlp.benchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import ru.babobka.nodeclient.ClientApplicationContainer;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodetester.benchmark.NodeBenchmarkCLI;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.TextUtil;

import java.util.Collections;
import java.util.List;

public class DlpNodeBenchmarkApp extends NodeBenchmarkCLI {

    private static final String ORDER_BIT_LENGTH_OPTION = "bitLength";
    private static final int MIN_BIT_LENGTH = 16;

    static {
        Container.getInstance().put(new ClientApplicationContainer());
    }

    @Override
    protected List<Option> createBenchmarkOptions() {
        Option numberBitLength = createRequiredArgOption(
                ORDER_BIT_LENGTH_OPTION, "Bit length of order of group. Must be at least " + MIN_BIT_LENGTH);
        return Collections.singletonList(numberBitLength);
    }

    @Override
    protected void extraBenchmarkValidation(CommandLine cmd) throws ParseException {
        String cmdNumberBitLength = cmd.getOptionValue(ORDER_BIT_LENGTH_OPTION);
        if (TextUtil.tryParseInt(cmdNumberBitLength, 0) < MIN_BIT_LENGTH) {
            throw new ParseException("invalid bit length " + cmdNumberBitLength);
        }
    }

    @Override
    protected void benchMarkRun(CommandLine cmd) {
        int orderBitLength = Integer.parseInt(cmd.getOptionValue(ORDER_BIT_LENGTH_OPTION));
        new NodeBenchmark(getAppName(), getTests(cmd))
                .run(getSlaves(cmd), getServiceThreads(cmd), new DlpNodeBenchmarkPerformer(orderBitLength));
    }

    @Override
    public String getAppName() {
        return "dlp-node-benchmark";
    }

    public static void main(String[] args) {
        new DlpNodeBenchmarkApp().onStart(args);
    }
}
