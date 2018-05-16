package ru.babobka.nodemasterserver.server.config;

import java.io.Serializable;

/**
 * Created by 123 on 13.05.2018.
 */
public class TimeoutConfig implements Serializable {
    private static final long serialVersionUID = 422090583720034558L;
    private int authTimeOutMillis;
    private int requestTimeOutMillis;
    private int heartBeatTimeOutMillis;

    public int getAuthTimeOutMillis() {
        return authTimeOutMillis;
    }

    public void setAuthTimeOutMillis(int authTimeOutMillis) {
        this.authTimeOutMillis = authTimeOutMillis;
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
}
