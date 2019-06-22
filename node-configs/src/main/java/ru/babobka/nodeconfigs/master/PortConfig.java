package ru.babobka.nodeconfigs.master;

import ru.babobka.nodeconfigs.NodeConfiguration;

import java.util.Objects;

/**
 * Created by 123 on 13.05.2018.
 */
public class PortConfig implements NodeConfiguration {

    private static final long serialVersionUID = -4706597948363441052L;
    private int slaveListenerPort;
    private int clientListenerPort;
    private int webListenerPort;

    public int getSlaveListenerPort() {
        return slaveListenerPort;
    }

    public void setSlaveListenerPort(int slaveListenerPort) {
        this.slaveListenerPort = slaveListenerPort;
    }

    public int getClientListenerPort() {
        return clientListenerPort;
    }

    public void setClientListenerPort(int clientListenerPort) {
        this.clientListenerPort = clientListenerPort;
    }

    public int getWebListenerPort() {
        return webListenerPort;
    }

    public void setWebListenerPort(int webListenerPort) {
        this.webListenerPort = webListenerPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortConfig that = (PortConfig) o;
        return slaveListenerPort == that.slaveListenerPort &&
                clientListenerPort == that.clientListenerPort &&
                webListenerPort == that.webListenerPort;
    }

    @Override
    public int hashCode() {
        return Objects.hash(slaveListenerPort, clientListenerPort, webListenerPort);
    }

    @Override
    public PortConfig copy() {
        PortConfig portConfig = new PortConfig();
        portConfig.setSlaveListenerPort(this.slaveListenerPort);
        portConfig.setClientListenerPort(this.clientListenerPort);
        portConfig.setWebListenerPort(this.webListenerPort);
        return portConfig;
    }
}
