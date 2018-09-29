package ru.babobka.nodeconfigs.service;

import ru.babobka.nodeconfigs.NodeConfiguration;
import ru.babobka.nodeconfigs.enums.ConfExt;
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
    private final SecureConfigService secureConfigService = Container.getInstance().get(SecureConfigService.class);

    public <T extends NodeConfiguration> T getConfig(String configPath, Class<T> configClass, String password) throws IOException {
        if (TextUtil.isEmpty(configPath)) {
            throw new IllegalArgumentException("cannot read configuration file with no file path");
        } else if (configClass == null) {
            throw new IllegalArgumentException("cannot read configuration file with no type");
        } else if (configPath.endsWith(ConfExt.ENCRYPTED.extension)) {
            if (TextUtil.isEmpty(password)) {
                throw new IllegalArgumentException("cannot read encrypted configuration file with no password being provided");
            }
            return secureConfigService.getConfig(configPath, configClass, password);
        }
        return JSONUtil.readJsonFile(streamUtil, configPath, configClass);
    }

    public void createConfig(String newConfigFolder, NodeConfiguration config, String password) throws IOException {
        if (TextUtil.isEmpty(newConfigFolder)) {
            throw new IllegalArgumentException("config folder path was not set");
        } else if (config == null) {
            throw new IllegalArgumentException("cannot create config out of null config object");
        }
        if (TextUtil.isEmpty(password)) {
            streamUtil.writeTextToFile(config.toString(), newConfigFolder + "/master-server-config" + ConfExt.JSON.extension);
        } else {
            secureConfigService.createConfig(newConfigFolder + "/master-server-config" + ConfExt.ENCRYPTED.extension,
                    config, password);
        }
    }
}