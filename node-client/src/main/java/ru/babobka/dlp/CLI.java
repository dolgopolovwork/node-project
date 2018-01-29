package ru.babobka.dlp;

import org.apache.commons.cli.*;

/**
 * Created by 123 on 28.01.2018.
 */
public abstract class CLI {

    protected static void printErr(String msg) {
        System.err.println(msg);
    }

    protected static void print(String msg) {
        System.out.println(msg);
    }

    protected abstract Options createCmdOptions();

    protected abstract void run(CommandLine cmd) throws Exception;

    protected abstract String getAppName();

    protected void extraValidation(CommandLine cmd) throws ParseException {
        //do nothing on default
    }

    protected void onMain(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options cmdOptions = createCmdOptions();
        CommandLine cmd;
        try {
            cmd = parser.parse(cmdOptions, args);
            extraValidation(cmd);
        } catch (ParseException e) {
            printErr(e.getMessage());
            new HelpFormatter().printHelp(getAppName(), cmdOptions);
            return;
        }
        try {
            run(cmd);
        } catch (Exception e) {
            printErr(e.getMessage());
        }
    }
}
