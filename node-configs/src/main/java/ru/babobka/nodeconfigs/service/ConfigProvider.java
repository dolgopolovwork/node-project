package ru.babobka.nodeconfigs.service;

import ru.babobka.nodeconfigs.NodeConfiguration;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

/**
 * Created by 123 on 09.09.2018.
 */
public class ConfigProvider {
    private final StreamUtil streamUtil = Container.getInstance().get(StreamUtil.class);

    public <T extends NodeConfiguration> T getConfig(String configPath, Class<T> configClass) throws IOException {
        if (TextUtil.isEmpty(configPath)) {
            throw new IllegalArgumentException("cannot read configuration file with no file path");
        } else if (configClass == null) {
            throw new IllegalArgumentException("cannot read configuration file with no type");
        }
        return JSONUtil.readJsonFile(streamUtil, configPath, configClass);
    }

    private void createConfig(
            String newConfigFolder, NodeConfiguration config, String configFileName) throws IOException {
        if (TextUtil.isEmpty(newConfigFolder)) {
            throw new IllegalArgumentException("config folder path was not set");
        } else if (config == null) {
            throw new IllegalArgumentException("cannot create config out of null config object");
        }
        streamUtil.writeTextToFile(config.toString(), newConfigFolder + configFileName);
    }

    public void createMasterConfig(String newConfigFolder, NodeConfiguration config) throws IOException {
        createConfig(newConfigFolder, config, "/master-server-config.json");
    }

    public void createSlaveConfig(String newConfigFolder, NodeConfiguration config) throws IOException {
        createConfig(newConfigFolder, config, "/slave-server-config.json");
    }
}
