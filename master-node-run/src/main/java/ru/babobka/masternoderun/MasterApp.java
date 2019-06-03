package ru.babobka.masternoderun;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
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
import java.util.Collections;
import java.util.List;

/**
 * Created by 123 on 06.12.2017.
 */
public class MasterApp extends CLI {

    private static final String CONFIG_PATH_OPTION = "masterConfigPath";

    private static void init() {
        Container.getInstance().put(container -> {
            container.put(TimerInvoker.createMaxOneSecondDelay());
            container.put(new StreamUtil());
            container.put(new MasterServerConfigValidator());
            container.put(new SecurityApplicationContainer());
            container.put(new ConfigsApplicationContainer());
        });
    }

    @Override
    public List<Option> createOptions() {
        Option configPath = createArgOption(
                CONFIG_PATH_OPTION,
                "Defines path to configuration json file. " +
                        "May be omitted, if environment variable " + Env.NODE_MASTER_CONFIG + " is set.");
        return Collections.singletonList(configPath);
    }

    @Override
    public void run(CommandLine cmd) throws IOException {
        String pathToConfig = getPathToConfig(cmd);
        MasterServerRunner masterServerRunner = new MasterServerRunner();
        masterServerRunner.run(pathToConfig);
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
        init();
        new MasterApp().onStart(args);
    }
}
