package ru.babobka.nodeslaveserver.server;

import java.io.Serializable;

public class SlaveServerConfig implements Serializable {

    private static final long serialVersionUID = 9130829367317179440L;
    private int serverPort;
    private String serverHost;
    private int requestTimeoutMillis;
    private int authTimeoutMillis;
    private String loggerFolder;
    private String tasksFolder;

    public int getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
    }

    public int getAuthTimeoutMillis() {
        return authTimeoutMillis;
    }

    public void setAuthTimeoutMillis(int authTimeoutMillis) {
        this.authTimeoutMillis = authTimeoutMillis;
    }

    public String getLoggerFolder() {
        return loggerFolder;
    }

    public void setLoggerFolder(String loggerFolder) {
        this.loggerFolder = loggerFolder;
    }

    public String getTasksFolder() {
        return tasksFolder;
    }

    public void setTasksFolder(String tasksFolder) {
        this.tasksFolder = tasksFolder;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    @Override
    public String toString() {
        return "SlaveServerConfig{" +
                "serverPort=" + serverPort +
                ", serverHost='" + serverHost + '\'' +
                ", requestTimeoutMillis=" + requestTimeoutMillis +
                ", authTimeoutMillis=" + authTimeoutMillis +
                ", loggerFolder='" + loggerFolder + '\'' +
                ", tasksFolder='" + tasksFolder + '\'' +
                '}';
    }
}
