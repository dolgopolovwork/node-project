package ru.babobka.masternoderun;

import com.google.gson.Gson;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

/**
 * Created by 123 on 05.12.2017.
 */
class ConfigFactory {

    private final Gson gson = Container.getInstance().get(Gson.class);
    private final StreamUtil streamUtil = Container.getInstance().get(StreamUtil.class);

    MasterServerConfig create(String pathToConfig) throws IOException {
        if (TextUtil.isEmpty(pathToConfig)) {
            throw new IllegalArgumentException("pathToConfig is null");
        }
        String fileContent = streamUtil.readFile(pathToConfig);
        try {
            return gson.fromJson(fileContent, MasterServerConfig.class);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
