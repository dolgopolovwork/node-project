package ru.babobka.nodeconfigs.master;

import java.io.Serializable;

public class RmqConfig implements Serializable {
    private static final long serialVersionUID = -6603260099830587487L;

    private String host;

    private int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
