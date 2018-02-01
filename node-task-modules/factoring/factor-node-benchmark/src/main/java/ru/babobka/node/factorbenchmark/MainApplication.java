package ru.babobka.node.factorbenchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.babobka.dlp.CLI;
import ru.babobka.dlp.ClientApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

public class MainApplication extends CLI {

    private static final String TESTS_OPTION = "tests";
    private static final String TESTS_OPT = "t";
    private static final String NUMBER_BIT_LENGTH_OPTION = "bitLength";
    private static final String NUMBER_BIT_LENGTH_OPT = "b";
    private static final String SLAVES_OPTION = "slaves";
    private static final String SLAVES_OPT = "s";
    private static final String SERVICE_THREADS_OPTION = "threads";
    private static final String SERVICE_THREADS_OPT = "st";
    private static final int MIN_BIT_LENGTH = 16;

    static {
        Container.getInstance().put(new ClientApplicationContainer());
    }

    @Override
    protected Options createCmdOptions() {
        Options options = new Options();
        Option tests = Option.builder(TESTS_OPT).longOpt(TESTS_OPTION).hasArg().
                desc("Tests to run").required().build();
        Option slaves = Option.builder(SLAVES_OPT).longOpt(SLAVES_OPTION).hasArg().
                desc("Slave nodes to run").build();
        Option threads = Option.builder(SERVICE_THREADS_OPT).longOpt(SERVICE_THREADS_OPTION).hasArg().
                desc("Threads to use per slave").build();
        Option numberBitLength = Option.builder(NUMBER_BIT_LENGTH_OPT).longOpt(NUMBER_BIT_LENGTH_OPTION).hasArg().
                desc("Bit length of numbers to factor. Must be at least " + MIN_BIT_LENGTH).required().build();
        options.addOption(tests).addOption(numberBitLength).addOption(slaves).addOption(threads);
        return options;
    }

    @Override
    protected void extraValidation(CommandLine cmd) throws ParseException {
        String cmdTests = cmd.getOptionValue(TESTS_OPTION);
        if (TextUtil.tryParseInt(cmdTests, 0) == 0) {
            throw new ParseException("invalid tests number " + cmdTests);
        }
        String cmdNumberBitLength = cmd.getOptionValue(NUMBER_BIT_LENGTH_OPTION);
        if (TextUtil.tryParseInt(cmdNumberBitLength, 0) < MIN_BIT_LENGTH) {
            throw new ParseException("invalid bit length " + cmdNumberBitLength);
        }
        String cmdSlaves = cmd.getOptionValue(SLAVES_OPTION);
        if (cmdSlaves != null && TextUtil.tryParseInt(cmdSlaves, 0) < 0) {
            throw new ParseException("invalid number of slaves " + cmdSlaves);
        }
        String cmdThreads = cmd.getOptionValue(SERVICE_THREADS_OPTION);
        if (cmdThreads != null && TextUtil.tryParseInt(cmdThreads, 0) < 1) {
            throw new ParseException("invalid number of threads " + cmdThreads);
        }
    }

    @Override
    protected void run(CommandLine cmd) {
        int test = Integer.parseInt(cmd.getOptionValue(TESTS_OPTION));
        int slaves = Integer.parseInt(cmd.getOptionValue(SLAVES_OPTION, "1"));
        int serviceThreads = Integer.parseInt(cmd.getOptionValue(SERVICE_THREADS_OPTION, String.valueOf(Runtime.getRuntime().availableProcessors())));
        int numberBitLength = Integer.parseInt(cmd.getOptionValue(NUMBER_BIT_LENGTH_OPTION));
        try {
            Container.getInstance().put(SimpleLogger.silentLogger("silent-log", TextUtil.getEnv("NODE_IFT_LOGS"), "benchmark"));
        } catch (IOException e) {
            printErr(e.getMessage());
            return;
        }
        print(slaves + " slave(s) with " + serviceThreads + " thread(s) per slave");
        new FactorNodeBenchmark(test, numberBitLength).run(slaves, serviceThreads);
    }

    @Override
    protected String getAppName() {
        return "factor-node-benchmark";
    }

    public static void main(String[] args) {
        new MainApplication().onMain(args);
    }
}
