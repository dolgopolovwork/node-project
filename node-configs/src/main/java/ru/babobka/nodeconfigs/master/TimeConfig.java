package ru.babobka.nodeconfigs.master;

import ru.babobka.nodeconfigs.NodeConfiguration;

import java.util.Objects;

/**
 * Created by 123 on 13.05.2018.
 */
public class TimeConfig implements NodeConfiguration {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeConfig that = (TimeConfig) o;
        return authTimeOutMillis == that.authTimeOutMillis &&
                requestReadTimeOutMillis == that.requestReadTimeOutMillis &&
                heartBeatCycleMillis == that.heartBeatCycleMillis &&
                dataOutDateMillis == that.dataOutDateMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(authTimeOutMillis, requestReadTimeOutMillis, heartBeatCycleMillis, dataOutDateMillis);
    }

    @Override
    public TimeConfig copy() {
        TimeConfig timeConfig = new TimeConfig();
        timeConfig.setDataOutDateMillis(this.dataOutDateMillis);
        timeConfig.setRequestReadTimeOutMillis(this.requestReadTimeOutMillis);
        timeConfig.setAuthTimeOutMillis(this.authTimeOutMillis);
        timeConfig.setHeartBeatCycleMillis(this.heartBeatCycleMillis);
        return timeConfig;
    }
}
