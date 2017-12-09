package ru.babobka.nodemasterserver.slave;

import java.io.Serializable;

class SlaveUser implements Serializable {

    private static final long serialVersionUID = -494398760871103796L;
    private final int localPort;
    private final int port;
    private final String address;

    private SlaveUser(int localPort, int port, String address) {
        this.localPort = localPort;
        this.port = port;
        this.address = address;
    }

    public SlaveUser(Slave slave) {
        this(slave.getConnection().getLocalPort(), slave.getConnection().getServerPort(),
                slave.getConnection().getHostName());
    }

    public int getLocalPort() {
        return localPort;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

}
