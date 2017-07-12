package ru.babobka.nodeslaveserver.builder;

import java.io.InputStream;

import org.json.JSONObject;

import ru.babobka.nodeslaveserver.exception.ServerConfigurationException;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.util.StreamUtil;

public interface JSONFileServerConfigBuilder {

    static SlaveServerConfig build(InputStream configFileInputStream) {

        try {
            return new SlaveServerConfig(new JSONObject(StreamUtil.readFile(configFileInputStream)));

        } catch (Exception e) {
            throw new ServerConfigurationException("Can not build server configuration", e);
        }

    }

}
