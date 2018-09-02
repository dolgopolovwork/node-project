package ru.babobka.nodeclient.console;

import org.apache.commons.cli.*;

/**
 * Created by 123 on 28.01.2018.
 */
public abstract class CLI {

    public static void printErr(String msg) {
        System.err.println(msg);
    }

    public static void print(String msg) {
        System.out.println(msg);
    }

    protected abstract Options createOptions();

    protected abstract void run(CommandLine cmd) throws Exception;

    protected abstract String getAppName();

    protected void extraValidation(CommandLine cmd) throws ParseException {
        //do nothing on default
    }

    protected void onMain(String[] args) {
        CommandLineParser parser = createParser();
        Options cmdOptions = createOptions();
        CommandLine cmd;
        try {
            cmd = parser.parse(cmdOptions, args);
            extraValidation(cmd);
        } catch (ParseException e) {
            printErr(e.getMessage());
            printHelp(cmdOptions);
            return;
        }
        try {
            run(cmd);
        } catch (Exception e) {
            e.printStackTrace();
            printErr(e.getMessage());
        }
    }

    CommandLineParser createParser() {
        return new DefaultParser();
    }

    void printHelp(Options cmdOptions) {
        new HelpFormatter().printHelp(getAppName(), cmdOptions);
    }
}
