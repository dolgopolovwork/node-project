package ru.babobka.nodeconfigs.dsa;

import ru.babobka.nodeconfigs.NodeConfiguration;

public class DSAManagerClientConfig implements NodeConfiguration {
    private static final long serialVersionUID = -1116074489572229102L;
    private int port;
    private String host;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public NodeConfiguration copy() {
        DSAManagerClientConfig config = new DSAManagerClientConfig();
        config.setHost(this.host);
        config.setPort(this.port);
        return config;
    }
}
