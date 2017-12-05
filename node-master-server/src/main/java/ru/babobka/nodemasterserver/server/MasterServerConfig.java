package ru.babobka.nodemasterserver.server;

import java.io.Serializable;

public class MasterServerConfig implements Serializable {

    private static final long serialVersionUID = 156081573106293600L;
    private int authTimeOutMillis;

    private int slaveListenerPort;

    private int clientListenerPort;

    private int requestTimeOutMillis;

    private int heartBeatTimeOutMillis;

    private int webListenerPort;

    private String restServiceLogin;

    private String restServiceHashedPassword;

    private String loggerFolder;

    private String tasksFolder;

    private boolean debugMode;

    public int getAuthTimeOutMillis() {
        return authTimeOutMillis;
    }

    public void setAuthTimeOutMillis(int authTimeOutMillis) {
        this.authTimeOutMillis = authTimeOutMillis;
    }

    public int getSlaveListenerPort() {
        return slaveListenerPort;
    }

    public void setSlaveListenerPort(int slaveListenerPort) {
        this.slaveListenerPort = slaveListenerPort;
    }

    public int getRequestTimeOutMillis() {
        return requestTimeOutMillis;
    }

    public void setRequestTimeOutMillis(int requestTimeOutMillis) {
        this.requestTimeOutMillis = requestTimeOutMillis;
    }

    public int getHeartBeatTimeOutMillis() {
        return heartBeatTimeOutMillis;
    }

    public void setHeartBeatTimeOutMillis(int heartBeatTimeOutMillis) {
        this.heartBeatTimeOutMillis = heartBeatTimeOutMillis;
    }

    public int getWebListenerPort() {
        return webListenerPort;
    }

    public void setWebListenerPort(int webListenerPort) {
        this.webListenerPort = webListenerPort;
    }

    public String getRestServiceLogin() {
        return restServiceLogin;
    }

    public void setRestServiceLogin(String restServiceLogin) {
        this.restServiceLogin = restServiceLogin;
    }

    public String getRestServiceHashedPassword() {
        return restServiceHashedPassword;
    }

    public void setRestServiceHashedPassword(String restServiceHashedPassword) {
        if (restServiceHashedPassword != null) {
            this.restServiceHashedPassword = restServiceHashedPassword;
        }
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

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public int getClientListenerPort() {
        return clientListenerPort;
    }

    public void setClientListenerPort(int clientListenerPort) {
        this.clientListenerPort = clientListenerPort;
    }

    @Override
    public String toString() {
        return "MasterServerConfig{" +
                "authTimeOutMillis=" + authTimeOutMillis +
                ", slaveListenerPort=" + slaveListenerPort +
                ", clientListenerPort=" + clientListenerPort +
                ", requestTimeOutMillis=" + requestTimeOutMillis +
                ", heartBeatTimeOutMillis=" + heartBeatTimeOutMillis +
                ", webListenerPort=" + webListenerPort +
                ", restServiceLogin='" + restServiceLogin + '\'' +
                ", restServiceHashedPassword='" + restServiceHashedPassword + '\'' +
                ", loggerFolder='" + loggerFolder + '\'' +
                ", tasksFolder='" + tasksFolder + '\'' +
                ", debugMode=" + debugMode +
                '}';
    }
}
