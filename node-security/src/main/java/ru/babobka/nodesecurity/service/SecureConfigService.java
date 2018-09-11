package ru.babobka.nodesecurity.service;

import ru.babobka.nodeutils.config.NodeConfiguration;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.ArrayUtil;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;

import javax.security.auth.login.Configuration;
import java.io.IOException;

/**
 * Created by 123 on 06.09.2018.
 */
public class SecureConfigService {
    private final SecureJSONService secureJSONService = Container.getInstance().get(SecureJSONService.class);
    private final StreamUtil streamUtil = Container.getInstance().get(StreamUtil.class);

    public <T extends NodeConfiguration> T getConfig(String configPath, Class<T> configClass, String password) throws IOException {
        if (TextUtil.isEmpty(password)) {
            throw new IllegalArgumentException("password was not set");
        } else if (TextUtil.isEmpty(configPath)) {
            throw new IllegalArgumentException("config path was not set");
        }
        byte[] configBytes = streamUtil.readBytesFromFile(configPath);
        if (ArrayUtil.isEmpty(configBytes)) {
            throw new IllegalArgumentException("cannot get config from '" + configPath + "'. file is empty.");
        }
        return secureJSONService.decrypt(configBytes, password, configClass);
    }

    public void createConfig(String newConfigPath, NodeConfiguration config, String password) throws IOException {
        if (TextUtil.isEmpty(password)) {
            throw new IllegalArgumentException("password was not set");
        } else if (TextUtil.isEmpty(newConfigPath)) {
            throw new IllegalArgumentException("config path was not set");
        } else if (config == null) {
            throw new IllegalArgumentException("cannot create secure config out of null config object");
        }
        byte[] encryptedConfig = secureJSONService.encrypt(config, password);
        streamUtil.writeBytesToFile(encryptedConfig, newConfigPath);
    }
}