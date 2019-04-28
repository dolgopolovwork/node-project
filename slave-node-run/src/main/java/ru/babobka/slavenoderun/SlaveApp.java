package ru.babobka.slavenoderun;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodeconfigs.slave.validation.SlaveServerConfigValidator;
import ru.babobka.nodeslaveserver.key.SlaveServerKey;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.slavenoderun.factory.PlainSlaveServerFactory;
import ru.babobka.slavenoderun.waiter.DummyWaiter;

import java.util.Collections;
import java.util.List;

/**
 * Created by 123 on 06.12.2017.
 */
public class SlaveApp extends CLI {
    private static final String CONFIG_PATH_OPTION = "slaveConfigPath";

    private static void init() {
        Container.getInstance().put(container -> {
            container.put(new StreamUtil());
            container.put(new SlaveServerConfigValidator());
            container.put(new PlainSlaveServerFactory());
            container.put(SlaveServerKey.SLAVE_CREATION_WAITER, new DummyWaiter());
        });
    }

    @Override
    public List<Option> createOptions() {
        Option configPath = createArgOption(
                CONFIG_PATH_OPTION,
                "Defines path to configuration json file. " +
                        "May be omitted, if environment variable " + Env.NODE_SLAVE_CONFIG + " is set.");
        return Collections.singletonList(configPath);
    }

    @Override
    public void run(CommandLine cmd) {
        String pathToConfig = getPathToConfig(cmd);
        new SlaveRunner().run(pathToConfig);
    }

    @Override
    public String getAppName() {
        return "slave-node-run";
    }

    private static String getPathToConfig(CommandLine cmd) {
        String configPath = cmd.getOptionValue(CONFIG_PATH_OPTION);
        if (configPath != null) {
            return configPath;
        }
        configPath = TextUtil.getEnv(Env.NODE_SLAVE_CONFIG);
        if (configPath != null) {
            print("Path to config was taken from environment variable " + Env.NODE_SLAVE_CONFIG);
            return configPath;
        }
        throw new IllegalArgumentException("Path to config was not set");
    }

    public static void main(String[] args) {
        init();
        new SlaveApp().onStart(args);
    }
}
