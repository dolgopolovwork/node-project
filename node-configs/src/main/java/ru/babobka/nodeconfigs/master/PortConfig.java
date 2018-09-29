package ru.babobka.nodeconfigs.master;

import java.io.Serializable;

/**
 * Created by 123 on 13.05.2018.
 */
public class PortConfig implements Serializable {

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
}
