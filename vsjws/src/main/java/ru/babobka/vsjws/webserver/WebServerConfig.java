package ru.babobka.vsjws.webserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by 123 on 01.01.2018.
 */
public class WebServerConfig {
    private static final Gson gson = new GsonBuilder().create();
    private String serverName;
    private int port;
    private int sessionTimeoutSeconds;
    private String logFolder;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSessionTimeoutSeconds() {
        return sessionTimeoutSeconds;
    }

    public void setSessionTimeoutSeconds(int sessionTimeoutSeconds) {
        this.sessionTimeoutSeconds = sessionTimeoutSeconds;
    }

    public String getLogFolder() {
        return logFolder;
    }

    public void setLogFolder(String logFolder) {
        this.logFolder = logFolder;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

}
