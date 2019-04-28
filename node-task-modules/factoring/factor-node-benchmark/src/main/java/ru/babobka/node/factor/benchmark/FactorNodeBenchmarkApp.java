package ru.babobka.node.factor.benchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodetester.benchmark.NodeBenchmarkCLI;
import ru.babobka.nodeutils.util.TextUtil;

import java.util.Collections;
import java.util.List;

public class FactorNodeBenchmarkApp extends NodeBenchmarkCLI {

    private static final String NUMBER_BIT_LENGTH_OPTION = "bitLength";
    private static final int MIN_BIT_LENGTH = 16;

    @Override
    protected List<Option> createBenchmarkOptions() {
        Option numberBitLength = createRequiredArgOption(
                NUMBER_BIT_LENGTH_OPTION, "Bit length of numbers to factor. Must be at least " + MIN_BIT_LENGTH);
        return Collections.singletonList(numberBitLength);
    }

    @Override
    protected void extraBenchmarkValidation(CommandLine cmd) throws ParseException {
        String cmdNumberBitLength = cmd.getOptionValue(NUMBER_BIT_LENGTH_OPTION);
        if (TextUtil.tryParseInt(cmdNumberBitLength, 0) < MIN_BIT_LENGTH) {
            throw new ParseException("invalid bit length " + cmdNumberBitLength);
        }
    }

    @Override
    protected void benchMarkRun(CommandLine cmd) {
        int numberBitLength = Integer.parseInt(cmd.getOptionValue(NUMBER_BIT_LENGTH_OPTION));
        new NodeBenchmark(getAppName(), getTests(cmd))
                .run(getSlaves(cmd), getServiceThreads(cmd), new FactorNodeBenchmarkPerformer(numberBitLength));
    }

    @Override
    public String getAppName() {
        return "factor-node-benchmark";
    }

    public static void main(String[] args) {
        new FactorNodeBenchmarkApp().onStart(args);
    }
}
