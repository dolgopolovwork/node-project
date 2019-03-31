package ru.babobka.nodetask.service;

import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeutils.time.Timer;

import java.io.Serializable;

public class TaskExecutionResult implements Serializable {

    private static final long serialVersionUID = -1759840716137209250L;
    private final long timeTakes;
    private final Data data = new Data();
    private final boolean wasStopped;

    TaskExecutionResult(long timeTakes,Data data, boolean wasStopped) {
        this.timeTakes = timeTakes;
        if (data != null) {
            this.data.put(data);
        }
        this.wasStopped = wasStopped;
    }

    public static TaskExecutionResult normal(Timer timer,Data data) {
        return new TaskExecutionResult(timer.getTimePassed(), data, false);
    }

    public static TaskExecutionResult stopped() {
        return new TaskExecutionResult(-1L, null, true);
    }

    public long getTimeTakes() {
        return timeTakes;
    }

    public boolean wasStopped() {
        return wasStopped;
    }

    public Data getData() {
        return data;
    }
}
