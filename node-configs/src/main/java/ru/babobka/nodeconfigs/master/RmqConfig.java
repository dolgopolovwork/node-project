package ru.babobka.nodeconfigs.master;

import ru.babobka.nodeconfigs.NodeConfiguration;

import java.util.Objects;

public class RmqConfig implements NodeConfiguration {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RmqConfig rmqConfig = (RmqConfig) o;
        return port == rmqConfig.port &&
                Objects.equals(host, rmqConfig.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public RmqConfig copy() {
        RmqConfig copy = new RmqConfig();
        copy.setPort(port);
        copy.setHost(host);
        return copy;
    }
}
