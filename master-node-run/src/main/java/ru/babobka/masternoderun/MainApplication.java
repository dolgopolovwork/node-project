package ru.babobka.masternoderun;

import org.apache.commons.cli.*;
import ru.babobka.nodemasterserver.validation.config.MasterServerConfigValidator;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.IOException;

/**
 * Created by 123 on 06.12.2017.
 */
public class MainApplication {

    private static final String ENV_VAR_CONFIG = "NODE_MASTER_CONFIG";
    private static final String CONFIG_PATH_OPTION = "configPath";
    private static final String CONFIG_PATH_OPT = "c";

    static {
        Container.getInstance().put(new StreamUtil());
        Container.getInstance().put(new MasterServerConfigValidator());
    }

    public static void main(String[] args) throws IOException {
        CommandLineParser parser = new DefaultParser();
        Options cmdOptions = createCmdOptions();
        try {
            CommandLine cmd = parser.parse(cmdOptions, args);
            String pathToConfig = getPathToConfig(cmd);
            MasterServerRunner masterServerRunner = new MasterServerRunner();
            masterServerRunner.run(pathToConfig);
        } catch (ParseException e) {
            printErr(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("master-node-run", cmdOptions);
        } catch (Exception e) {
            printErr("Error occurred while startup. " + e.getMessage());
        }
    }

    private static Options createCmdOptions() {
        Options options = new Options();
        Option configPath = Option.builder(CONFIG_PATH_OPT).longOpt(CONFIG_PATH_OPTION).hasArg().desc("Defines path to configuration json file. May be omitted, if environment variable " + ENV_VAR_CONFIG + " is set.").build();
        options.addOption(configPath);
        return options;
    }

    private static void printErr(String msg) {
        System.err.println(msg);
    }

    private static void print(String msg) {
        System.out.println(msg);
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

}
