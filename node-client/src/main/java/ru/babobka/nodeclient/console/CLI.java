package ru.babobka.nodeclient.console;

import lombok.NonNull;
import org.apache.commons.cli.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 123 on 28.01.2018.
 */
public abstract class CLI extends CLIUtils {

    private final AtomicBoolean started = new AtomicBoolean(false);

    public abstract List<Option> createOptions();

    public abstract void run(CommandLine cmd) throws Exception;

    public abstract String getAppName();

    public void extraValidation(CommandLine cmd) throws ParseException {
        //do nothing on default
    }

    public void onStart(String[] args) {
        checkIfWasStarted();
        Options cmdOptions = buildOptions();
        CommandLine cmd;
        try {
            cmd = parseCMD(cmdOptions, args);
            extraValidation(cmd);
        } catch (ParseException | RuntimeException e) {
            printErr(e.getMessage());
            printHelp(cmdOptions);
            return;
        }
        try {
            run(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CommandLine parseCMD(Options options, String[] args) throws ParseException {
        CommandLineParser parser = createParser();
        return parser.parse(options, args);
    }

    Options buildOptions() {
        Options cmdOptions = new Options();
        createOptions().forEach(cmdOptions::addOption);
        return cmdOptions;
    }

    private void checkIfWasStarted() {
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("Cannot start CLI '" + getAppName() + "' twice");
        }
    }

    private CommandLineParser createParser() {
        return new DefaultParser();
    }

    void printHelp(Options cmdOptions) {
        new HelpFormatter().printHelp(getAppName(), cmdOptions);
    }
}
