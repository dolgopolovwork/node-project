package ru.babobka.nodeconfigs.service;

import lombok.NonNull;
import ru.babobka.nodeconfigs.NodeConfiguration;
import ru.babobka.nodeconfigs.exception.EnvConfigCreationException;
import ru.babobka.nodeconfigs.utils.EnvBasedConfigUtil;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.util.YamlUtil;

import java.io.IOException;

/**
 * Created by 123 on 09.09.2018.
 */
public class ConfigProvider {

    public <T extends NodeConfiguration> T getConfig(@NonNull String configPath, @NonNull Class<T> configClass) throws IOException, EnvConfigCreationException {
        if (TextUtil.isEmpty(configPath)) {
            throw new IllegalArgumentException("cannot read configuration file with no file path");
        }
        T config = YamlUtil.read(configPath, configClass);
        return EnvBasedConfigUtil.buildFromEnv(config);
    }

}
