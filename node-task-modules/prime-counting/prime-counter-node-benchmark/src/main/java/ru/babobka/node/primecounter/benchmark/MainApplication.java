package ru.babobka.node.primecounter.benchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodetester.benchmark.NodeBenchmarkCLI;
import ru.babobka.nodeutils.util.TextUtil;

public class MainApplication extends NodeBenchmarkCLI {

    private static final String BEGIN_OPTION = "begin";
    private static final String BEGIN_OPT = "b";
    private static final String END_OPTION = "end";
    private static final String END_OPT = "e";
    private static final int MIN_LENGTH = 1000;

    @Override
    protected Options createBenchmarkOptions() {
        Options options = new Options();
        Option begin = Option.builder(BEGIN_OPT).longOpt(BEGIN_OPTION).hasArg().
                desc("Begin range").required().build();
        Option end = Option.builder(END_OPT).longOpt(END_OPTION).hasArg().
                desc("End range").required().build();
        options.addOption(begin).addOption(end);
        return options;
    }

    @Override
    protected void extraBenchmarkValidation(CommandLine cmd) throws ParseException {
        String cmdBegin = cmd.getOptionValue(BEGIN_OPTION);
        int begin = TextUtil.tryParseInt(cmdBegin, 0);
        if (begin < 0) {
            throw new ParseException("invalid begin " + cmdBegin);
        }
        String cmdEnd = cmd.getOptionValue(END_OPTION);
        int end = TextUtil.tryParseInt(cmdEnd, 0);
        if (end < 0) {
            throw new ParseException("invalid end " + cmdBegin);
        }
        if (begin >= end) {
            throw new ParseException("begin is bigger than end");
        } else if (end - begin < MIN_LENGTH) {
            throw new ParseException("range is too small. need at least " + MIN_LENGTH + " elements");
        }
    }

    @Override
    protected void benchMarkRun(CommandLine cmd) {
        int begin = Integer.parseInt(cmd.getOptionValue(BEGIN_OPTION));
        int end = Integer.parseInt(cmd.getOptionValue(END_OPTION));
        new NodeBenchmark(getAppName(), getTests(cmd)).run(getSlaves(cmd), getServiceThreads(cmd), new PrimeCounterBenchmarkPerformer(begin, end));
    }

    @Override
    protected String getAppName() {
        return "prime-counter-node-benchmark";
    }

    public static void main(String[] args) {
        new MainApplication().onMain(args);
    }
}
