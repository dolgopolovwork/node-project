package ru.babobka.nodeserials;

import ru.babobka.nodeserials.data.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by 123 on 27.08.2017.
 */
public class NodeData implements Serializable {
    private static final long serialVersionUID = 8L;
    static final UUID DUMMY_UUID = new UUID(0, 0);
    private final UUID id;
    private final UUID taskId;
    private final String taskName;
    private long timeStamp;
    private final Data data = new Data();

    public NodeData(UUID id, UUID taskId, String taskName, long timeStamp, Data data) {
        this.id = id;
        this.taskId = taskId;
        this.taskName = taskName;
        if (data != null) {
            this.data.put(data);
        }
        setTimeStamp(timeStamp);
    }

    public UUID getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public synchronized long getTimeStamp() {
        return timeStamp;
    }

    public synchronized void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Data getData() {
        return data;
    }

    public UUID getId() {
        return id;
    }

    public <T> T getDataValue(String key) {
        return getDataValue(key, null);
    }

    public <T> T getDataValue(String key, T defaultValue) {
        Serializable value = data.get(key);
        if (value == null) {
            return defaultValue;
        }
        return (T) value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeData nodeData = (NodeData) o;

        if (timeStamp != nodeData.timeStamp) return false;
        if (id != null ? !id.equals(nodeData.id) : nodeData.id != null) return false;
        if (taskId != null ? !taskId.equals(nodeData.taskId) : nodeData.taskId != null) return false;
        return (taskName != null ? taskName.equals(nodeData.taskName) : nodeData.taskName == null) && (data != null ? data.equals(nodeData.data) : nodeData.data == null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (taskId != null ? taskId.hashCode() : 0);
        result = 31 * result + (taskName != null ? taskName.hashCode() : 0);
        result = 31 * result + (int) (timeStamp ^ (timeStamp >>> 32));
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NodeData{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", timeStamp=" + timeStamp +
                ", data=" + data +
                '}';
    }
}
