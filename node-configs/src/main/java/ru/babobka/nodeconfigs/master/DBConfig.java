package ru.babobka.nodeconfigs.master;

import ru.babobka.nodeconfigs.NodeConfiguration;

public class DBConfig implements NodeConfiguration {
    private static final long serialVersionUID = 1742117609183677035L;
    private String host;
    private int port;
    private String user;
    private String password;

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public DBConfig copy() {
        DBConfig copy = new DBConfig();
        copy.setHost(host);
        copy.setPort(port);
        copy.setUser(user);
        copy.setPassword(password);
        return copy;
    }
}
