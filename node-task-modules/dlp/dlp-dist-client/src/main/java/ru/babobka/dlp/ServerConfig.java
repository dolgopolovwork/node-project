package ru.babobka.dlp;

import ru.babobka.nodeutils.util.TextUtil;

/**
 * Created by 123 on 22.07.2018.
 */
public class ServerConfig {
    private final String host;
    private final int clientPort;
    private final int webPort;

    public ServerConfig(String host, int clientPort, int webPort) {
        if (TextUtil.isEmpty(host)) {
            throw new IllegalArgumentException("host was not set");
        } else if (!TextUtil.isValidPort(clientPort)) {
            throw new IllegalArgumentException("invalid client port " + clientPort);
        } else if (!TextUtil.isValidPort(webPort)) {
            throw new IllegalArgumentException("invalid web port " + webPort);
        } else if (clientPort == webPort) {
            throw new IllegalArgumentException("client port can not be equal to web port");
        }
        this.host = host;
        this.clientPort = clientPort;
        this.webPort = webPort;
    }

    public String getHost() {
        return host;
    }

    public int getClientPort() {
        return clientPort;
    }

    public int getWebPort() {
        return webPort;
    }
}
