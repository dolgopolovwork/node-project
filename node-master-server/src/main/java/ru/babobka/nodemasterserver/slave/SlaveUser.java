package ru.babobka.nodemasterserver.slave;

import java.io.Serializable;

class SlaveUser implements Serializable{

    private static final long serialVersionUID = -494398760871103796L;
    private final String login;

    private final int localPort;

    private final int port;

    private final String address;

    private final int requestCount;

    SlaveUser(String login, int localPort, int port, String address, int requestCount) {
	this.login = login;
	this.localPort = localPort;
	this.port = port;
	this.address = address;
	this.requestCount = requestCount;
    }

    public String getLogin() {
	return login;
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

    public int getRequestCount() {
	return requestCount;
    }

}
