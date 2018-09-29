package ru.babobka.nodeconfigs.master;

import java.io.Serializable;

/**
 * Created by 123 on 13.05.2018.
 */
public class TimeConfig implements Serializable {
    private static final long serialVersionUID = 422090583720034558L;
    private int authTimeOutMillis;
    private int requestReadTimeOutMillis;
    private int heartBeatCycleMillis;
    private int dataOutDateMillis;

    public int getAuthTimeOutMillis() {
        return authTimeOutMillis;
    }

    public void setAuthTimeOutMillis(int authTimeOutMillis) {
        this.authTimeOutMillis = authTimeOutMillis;
    }

    public int getRequestReadTimeOutMillis() {
        return requestReadTimeOutMillis;
    }

    public void setRequestReadTimeOutMillis(int requestReadTimeOutMillis) {
        this.requestReadTimeOutMillis = requestReadTimeOutMillis;
    }

    public int getHeartBeatCycleMillis() {
        return heartBeatCycleMillis;
    }

    public void setHeartBeatCycleMillis(int heartBeatCycleMillis) {
        this.heartBeatCycleMillis = heartBeatCycleMillis;
    }

    public int getDataOutDateMillis() {
        return dataOutDateMillis;
    }

    public void setDataOutDateMillis(int dataOutDateMillis) {
        this.dataOutDateMillis = dataOutDateMillis;
    }
}
