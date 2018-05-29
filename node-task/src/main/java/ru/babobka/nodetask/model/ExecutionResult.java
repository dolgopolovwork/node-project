package ru.babobka.nodetask.model;

import ru.babobka.nodeserials.data.Data;

/**
 * Created by dolgopolov.a on 29.09.15.
 */
public class ExecutionResult {

    private final boolean stopped;

    private final Data data = new Data();

    public ExecutionResult(boolean stopped, Data data) {
        this.stopped = stopped;
        if (data != null)
            this.data.put(data);
    }

    public static ExecutionResult ok(Data data) {
        return new ExecutionResult(false, data);
    }

    public static ExecutionResult stopped() {
        return new ExecutionResult(true, null);
    }

    public boolean isStopped() {
        return stopped;
    }

    public Data getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ExecutionResult{" +
                "stopped=" + stopped +
                ", data=" + data +
                '}';
    }
}
