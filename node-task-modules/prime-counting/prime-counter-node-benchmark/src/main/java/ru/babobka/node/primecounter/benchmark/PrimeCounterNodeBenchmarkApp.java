package ru.babobka.node.primecounter.benchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodetester.benchmark.NodeBenchmarkCLI;
import ru.babobka.nodeutils.util.TextUtil;

import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

public class PrimeCounterNodeBenchmarkApp extends NodeBenchmarkCLI {

    private static final String BEGIN_OPTION = "begin";
    private static final String END_OPTION = "end";
    private static final int MIN_LENGTH = 1000;

    @Override
    protected List<Option> createBenchmarkOptions() {
        Option begin = createRequiredArgOption(BEGIN_OPTION, "Begin range");
        Option end = createRequiredArgOption(END_OPTION, "End range");
        return Arrays.asList(begin, end);
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
    protected void benchmarkRun(CommandLine cmd) {
        int begin = Integer.parseInt(cmd.getOptionValue(BEGIN_OPTION));
        int end = Integer.parseInt(cmd.getOptionValue(END_OPTION));
        new NodeBenchmark(getAppName(), getTests(cmd))
                .run(getSlaves(cmd), getServiceThreads(cmd), new PrimeCounterBenchmarkPerformer(begin, end));
    }

    @Override
    public String getAppName() {
        return "prime-counter-node-benchmark";
    }

    public static void main(String[] args) {
        new PrimeCounterNodeBenchmarkApp().onStart(args);
    }
}
