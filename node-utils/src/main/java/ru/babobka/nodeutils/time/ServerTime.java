package ru.babobka.nodeutils.time;

import java.io.Serializable;

/**
 * Created by 123 on 06.06.2018.
 */
public class ServerTime implements Serializable {
    private static final long serialVersionUID = 8493038523212258079L;
    private final long initTime = System.currentTimeMillis();
    private final long remoteServerTime;

    public ServerTime(long remoteServerTime) {
        this.remoteServerTime = remoteServerTime;
    }

    public long getTime() {
        long timePassed = System.currentTimeMillis() - initTime;
        return remoteServerTime + timePassed;
    }
}
