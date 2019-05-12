package ru.babobka.nodetester.benchmark;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import ru.babobka.nodebusiness.StorageApplicationContainer;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodetester.key.TesterKey;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.ClassLoaderUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.File;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 123 on 03.02.2018.
 */
public abstract class NodeBenchmarkCLI extends CLI {

    static {
        LoggerInit.initPersistentNoConsoleLogger(TextUtil.getEnv(Env.NODE_LOGS), "benchmark");
    }

    private static final String TESTS_OPTION = "tests";
    private static final String SLAVES_OPTION = "slaves";
    private static final String SERVICE_THREADS_OPTION = "threads";
    private static final String CACHE_OPTION = "cache";
    private static final String PERMANENT_DRIVER_OPTION = "perm";

    @Override
    public List<Option> createOptions() {
        Option tests = createRequiredArgOption(TESTS_OPTION, "Tests to run");
        Option slaves = createArgOption(SLAVES_OPTION, "Slave nodes to run");
        Option threads = createArgOption(SERVICE_THREADS_OPTION, "Threads to use per slave");
        Option cache = createFlagOption(CACHE_OPTION, "Enables cache");
        Option permanentDriver = createArgOption(PERMANENT_DRIVER_OPTION, "Enables benchmark result storage. " +
                "Requires path to driver as an argument");
        List<Option> options = new ArrayList<>();
        options.addAll(Arrays.asList(tests, slaves, threads, cache, permanentDriver));
        options.addAll(createBenchmarkOptions());
        return options;
    }

    protected abstract List<Option> createBenchmarkOptions();

    @Override
    public void extraValidation(CommandLine cmd) throws ParseException {
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
    public void run(CommandLine cmd) {
        Container container = Container.getInstance();
        try {
            Properties.put(TesterKey.ENABLE_CACHE, cmd.hasOption(CACHE_OPTION));
            if (cmd.hasOption(PERMANENT_DRIVER_OPTION)) {
                Properties.put(TesterKey.PERMANENT, true);
                ClassLoaderUtil.addPath(cmd.getOptionValue(PERMANENT_DRIVER_OPTION));
                container.put(new StorageApplicationContainer());
            }
        } catch (IOException e) {
            printErr(e.getMessage());
            return;
        }
        print(getSlaves(cmd) + " slave(s) with " + getServiceThreads(cmd) + " thread(s) per slave");
        benchmarkRun(cmd);
    }

    protected abstract void benchmarkRun(CommandLine cmd);

}
