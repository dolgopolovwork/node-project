package ru.babobka.masternoderun;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodemasterserver.validation.config.MasterServerConfigValidator;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

/**
 * Created by 123 on 06.12.2017.
 */
public class MainApplication extends CLI {

    private static final String CONFIG_PATH_OPTION = "configPath";
    private static final String CONFIG_PATH_OPT = "c";

    static {
        Container.getInstance().put(container -> {
            container.put(new StreamUtil());
            container.put(new MasterServerConfigValidator());
        });
    }

    @Override
    protected Options createOptions() {
        Options options = new Options();
        Option configPath = Option.builder(CONFIG_PATH_OPT).longOpt(CONFIG_PATH_OPTION).hasArg().
                desc("Defines path to configuration json file. May be omitted, if environment variable " + Env.NODE_MASTER_CONFIG + " is set.").build();
        options.addOption(configPath);
        return options;
    }

    @Override
    protected void run(CommandLine cmd) throws IOException {
        String pathToConfig = getPathToConfig(cmd);
        MasterServerRunner masterServerRunner = new MasterServerRunner();
        masterServerRunner.run(pathToConfig);
    }

    @Override
    protected String getAppName() {
        return "master-node-run";
    }

    private static String getPathToConfig(CommandLine cmd) {
        String configPath = cmd.getOptionValue(CONFIG_PATH_OPTION);
        if (configPath != null) {
            return configPath;
        }
        configPath = TextUtil.getEnv(Env.NODE_MASTER_CONFIG);
        if (configPath != null) {
            print("Path to config was taken from environment variable " + Env.NODE_MASTER_CONFIG);
            return configPath;
        }
        throw new IllegalArgumentException("Path to config was not set");
    }

    public static void main(String[] args) {
        new MainApplication().onMain(args);
    }
}
