package ru.babobka.nodetester.benchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.babobka.nodebusiness.StorageApplicationContainer;
import ru.babobka.nodeclient.CLI;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.ClassLoaderUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by 123 on 03.02.2018.
 */
public abstract class NodeBenchmarkCLI extends CLI {
    private static final String TESTS_OPTION = "tests";
    private static final String TESTS_OPT = "t";
    private static final String SLAVES_OPTION = "slaves";
    private static final String SLAVES_OPT = "s";
    private static final String SERVICE_THREADS_OPTION = "threads";
    private static final String SERVICE_THREADS_OPT = "st";
    private static final String CACHE_OPTION = "cache";
    private static final String CACHE_OPT = "c";
    private static final String PERMANENT_DRIVER_OPTION = "perm";

    @Override
    protected Options createOptions() {
        Options options = new Options();
        Option tests = Option.builder(TESTS_OPT).longOpt(TESTS_OPTION).hasArg().
                desc("Tests to run").required().build();
        Option slaves = Option.builder(SLAVES_OPT).longOpt(SLAVES_OPTION).hasArg().
                desc("Slave nodes to run").build();
        Option threads = Option.builder(SERVICE_THREADS_OPT).longOpt(SERVICE_THREADS_OPTION).hasArg().
                desc("Threads to use per slave").build();
        Option cache = Option.builder(CACHE_OPT).longOpt(CACHE_OPTION).
                desc("Enables cache").build();
        Option permanentDriver = Option.builder().longOpt(PERMANENT_DRIVER_OPTION).
                desc("Enables benchmark result storage. Requires path to driver as an argument.").hasArg().build();
        options.addOption(tests).addOption(slaves).addOption(threads).addOption(cache).addOption(permanentDriver);
        Options benchmarkOptions = createBenchmarkOptions();
        if (benchmarkOptions != null) {
            for (Option benchmarkOption : benchmarkOptions.getOptions()) {
                options.addOption(benchmarkOption);
            }
        }
        return options;
    }

    protected abstract Options createBenchmarkOptions();

    @Override
    protected void extraValidation(CommandLine cmd) throws ParseException {
        String cmdTests = cmd.getOptionValue(TESTS_OPTION);
        if (TextUtil.tryParseInt(cmdTests, 0) == 0) {
            throw new ParseException("invalid tests number " + cmdTests);
        }
        String cmdSlaves = cmd.getOptionValue(SLAVES_OPTION);
        if (cmdSlaves != null && TextUtil.tryParseInt(cmdSlaves, 0) < 0) {
            throw new ParseException("invalid number of slaves " + cmdSlaves);
        }
        String cmdThreads = cmd.getOptionValue(SERVICE_THREADS_OPTION);
        if (cmdThreads != null && TextUtil.tryParseInt(cmdThreads, 0) < 1) {
            throw new ParseException("invalid number of threads " + cmdThreads);
        }
        String cmdPermanentDriver = cmd.getOptionValue(PERMANENT_DRIVER_OPTION);
        if (cmdPermanentDriver != null && !new File(cmdPermanentDriver).exists()) {
            throw new ParseException("file " + cmdPermanentDriver + " doesn't exists");
        }
        extraBenchmarkValidation(cmd);
    }

    protected void extraBenchmarkValidation(CommandLine cmd) throws ParseException {
        //do nothing by default
    }

    protected int getTests(CommandLine cmd) {
        return Integer.parseInt(cmd.getOptionValue(TESTS_OPTION));
    }

    protected int getSlaves(CommandLine cmd) {
        return Integer.parseInt(cmd.getOptionValue(SLAVES_OPTION, "1"));
    }

    protected int getServiceThreads(CommandLine cmd) {
        return Integer.parseInt(cmd.getOptionValue(SERVICE_THREADS_OPTION, String.valueOf(Runtime.getRuntime().availableProcessors())));
    }

    @Override
    protected void run(CommandLine cmd) {
        Container container = Container.getInstance();
        try {
            TextUtil.hideWarnings("SLF4J");
            container.put(SimpleLogger.silentLogger("silent-log", TextUtil.getEnv("NODE_LOGS")));
            container.put("enableCache", cmd.hasOption(CACHE_OPTION));
            if (cmd.hasOption(PERMANENT_DRIVER_OPTION)) {
                container.put("permanent", true);
                ClassLoaderUtil.addPath(cmd.getOptionValue(PERMANENT_DRIVER_OPTION));
                container.put(new StorageApplicationContainer());
            }
        } catch (IOException e) {
            printErr(e.getMessage());
            return;
        }
        print(getSlaves(cmd) + " slave(s) with " + getServiceThreads(cmd) + " thread(s) per slave");
        benchMarkRun(cmd);
    }

    protected abstract void benchMarkRun(CommandLine cmd);

}
