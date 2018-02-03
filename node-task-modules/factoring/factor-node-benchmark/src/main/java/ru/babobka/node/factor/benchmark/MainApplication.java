package ru.babobka.node.factor.benchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.babobka.nodetester.benchmark.NodeBenchmarkCLI;
import ru.babobka.nodeutils.util.TextUtil;

public class MainApplication extends NodeBenchmarkCLI {

    private static final String NUMBER_BIT_LENGTH_OPTION = "bitLength";
    private static final String NUMBER_BIT_LENGTH_OPT = "b";
    private static final int MIN_BIT_LENGTH = 16;

    @Override
    protected Options createBenchmarkOptions() {
        Options options = new Options();
        Option numberBitLength = Option.builder(NUMBER_BIT_LENGTH_OPT).longOpt(NUMBER_BIT_LENGTH_OPTION).hasArg().
                desc("Bit length of numbers to factor. Must be at least " + MIN_BIT_LENGTH).required().build();
        options.addOption(numberBitLength);
        return options;
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
        new FactorNodeBenchmark(getTests(cmd), numberBitLength).run(getSlaves(cmd), getServiceThreads(cmd));
    }

    @Override
    protected String getAppName() {
        return "factor-node-benchmark";
    }

    public static void main(String[] args) {
        new MainApplication().onMain(args);
    }
}
