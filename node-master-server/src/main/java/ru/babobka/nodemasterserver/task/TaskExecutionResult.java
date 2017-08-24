package ru.babobka.nodemasterserver.task;

import ru.babobka.nodeutils.time.Timer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TaskExecutionResult {

    private final long timeTakes;
    private final Map<String, Serializable> result = new HashMap<>();
    private final boolean wasStopped;

    TaskExecutionResult(long timeTakes, Map<String, Serializable> result, boolean wasStopped) {
        this.timeTakes = timeTakes;
        if (result != null) {
            this.result.putAll(result);
        }
        this.wasStopped = wasStopped;
    }

    public static TaskExecutionResult normal(Timer timer, Map<String, Serializable> result) {
        return new TaskExecutionResult(timer.getTimePassed(), result, false);
    }

    public static TaskExecutionResult stopped() {
        return new TaskExecutionResult(-1L, null, true);
    }

    public long getTimeTakes() {
        return timeTakes;
    }

    public Map<String, Serializable> getResult() {
        return result;
    }

    public boolean isWasStopped() {
        return wasStopped;
    }
}
