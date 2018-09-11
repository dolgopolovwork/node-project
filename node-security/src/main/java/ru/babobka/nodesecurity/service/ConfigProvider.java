package ru.babobka.nodesecurity.service;

import ru.babobka.nodeutils.config.NodeConfiguration;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

/**
 * Created by 123 on 09.09.2018.
 */
public class ConfigProvider {
    public static final String ENCRYPTED_CONFIG_EXT = ".encrypted";
    private final StreamUtil streamUtil = Container.getInstance().get(StreamUtil.class);
    private final SecureConfigService secureConfigService = Container.getInstance().get(SecureConfigService.class);

    public <T extends NodeConfiguration> T getConfig(String configPath, Class<T> configClass, String password) throws IOException {
        if (TextUtil.isEmpty(configPath)) {
            throw new IllegalArgumentException("cannot read configuration file with no file path");
        } else if (configClass == null) {
            throw new IllegalArgumentException("cannot read configuration file with no type");
        } else if (configPath.endsWith(ENCRYPTED_CONFIG_EXT)) {
            if (TextUtil.isEmpty(password)) {
                throw new IllegalArgumentException("cannot read encrypted configuration file with no password being provided");
            }
            return secureConfigService.getConfig(configPath, configClass, password);
        }
        return JSONUtil.readJsonFile(streamUtil, configPath, configClass);
    }
}