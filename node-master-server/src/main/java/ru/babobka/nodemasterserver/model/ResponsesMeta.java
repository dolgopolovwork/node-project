package ru.babobka.nodemasterserver.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ResponsesMeta implements Serializable {

    private static final long serialVersionUID = 6357739718733111325L;
    private final String taskName;
    private final Map<String, Serializable> data = new HashMap<>();
    private final long startTime;

    ResponsesMeta(String taskName, Map<String, Serializable> data, long startTime) {
        this.taskName = taskName;
        if (data != null) {
            this.data.putAll(data);
        }
        this.startTime = startTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public Map<String, Serializable> getData() {
        return data;
    }

    public long getStartTime() {
        return startTime;
    }

    public Date getStartDate() {
        return new Date(startTime);
    }

}
