package ru.babobka.slavenoderun;

import org.apache.commons.cli.*;
import ru.babobka.nodeslaveserver.validator.config.SlaveServerConfigValidator;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.IOException;

/**
 * Created by 123 on 06.12.2017.
 */
public class MainApplication {
    private static final String CONFIG_PATH_OPTION = "configPath";
    private static final String LOGIN_OPTION = "login";
    private static final String PASSWORD_OPTION = "password";
    private static final String CONFIG_PATH_OPT = "c";
    private static final String LOGIN_OPT = "l";
    private static final String PASSWORD_OPT = "p";
    private static final String ENV_VAR_CONFIG = "NODE_SLAVE_CONFIG";

    static {
        Container.getInstance().put(new StreamUtil());
        Container.getInstance().put(new SlaveServerConfigValidator());
        Container.getInstance().put(new SlaveServerFactory());
    }

    public static void main(String[] args) throws IOException {
        CommandLineParser parser = new DefaultParser();
        Options cmdOptions = createCmdOptions();
        try {
            CommandLine cmd = parser.parse(cmdOptions, args);
            String login = cmd.getOptionValue(LOGIN_OPTION);
            String password = cmd.getOptionValue(PASSWORD_OPTION);
            String pathToConfig = getPathToConfig(cmd);
            new ConnectionSafeSlaveRunner().run(pathToConfig, login, password);
        } catch (ParseException e) {
            printErr(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("slave-node-run", cmdOptions);
        } catch (Exception e) {
            printErr(e.getMessage());
        }
    }

    private static Options createCmdOptions() {
        Options options = new Options();
        Option configPath = Option.builder(CONFIG_PATH_OPT).longOpt(CONFIG_PATH_OPTION).hasArg().desc("Defines path to configuration json file. May be omitted, if environment variable " + ENV_VAR_CONFIG + " is set.").build();
        Option loginOption = Option.builder(LOGIN_OPT).longOpt(LOGIN_OPTION).hasArg().desc("Slave login").required().build();
        Option passwordOption = Option.builder(PASSWORD_OPT).longOpt(PASSWORD_OPTION).hasArg().desc("Slave password").required().build();
        options.addOption(configPath);
        options.addOption(loginOption);
        options.addOption(passwordOption);
        return options;
    }

    private static String getPathToConfig(CommandLine cmd) {
        String configPath = cmd.getOptionValue(CONFIG_PATH_OPTION);
        if (configPath != null) {
            return configPath;
        }
        configPath = System.getenv(ENV_VAR_CONFIG);
        if (configPath != null) {
            print("Path to config was taken from environment variable " + ENV_VAR_CONFIG);
            return configPath;
        }
        throw new IllegalArgumentException("Path to config was not set");
    }


    private static void printErr(String msg) {
        System.err.println(msg);
    }

    private static void print(String msg) {
        System.out.println(msg);
    }


}
