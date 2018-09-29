package ru.babobka.masternoderun;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodeconfigs.ConfigsApplicationContainer;
import ru.babobka.nodeconfigs.master.validation.MasterServerConfigValidator;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.time.TimerInvoker;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

/**
 * Created by 123 on 06.12.2017.
 */
public class MainApplication extends CLI {

    private static final String CONFIG_PATH_OPTION = "configPath";
    private static final String CONFIG_PASSWORD_OPTION = "configPassword";

    static {
        Container.getInstance().put(container -> {
            container.put(TimerInvoker.createMaxOneSecondDelay());
            container.put(new StreamUtil());
            container.put(new MasterServerConfigValidator());
            container.put(new SecurityApplicationContainer());
            container.put(new ConfigsApplicationContainer());
        });
    }

    @Override
    protected Options createOptions() {
        Options options = new Options();
        Option configPath = Option.builder().longOpt(CONFIG_PATH_OPTION).hasArg().
                desc("Defines path to configuration json file. May be omitted, if environment variable " + Env.NODE_MASTER_CONFIG + " is set.").build();
        options.addOption(configPath);
        Option configPassword = Option.builder().longOpt(CONFIG_PASSWORD_OPTION).hasArg().
                desc("Defines password for decryption of configuration file. Not used if configuration file is not encrypted.").build();
        options.addOption(configPassword);
        return options;
    }

    @Override
    protected void run(CommandLine cmd) throws IOException {
        String pathToConfig = getPathToConfig(cmd);
        String configPassword = cmd.getOptionValue(CONFIG_PASSWORD_OPTION);
        MasterServerRunner masterServerRunner = new MasterServerRunner();
        masterServerRunner.run(pathToConfig, configPassword);
    }

    @Override
    public String getAppName() {
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
