package ru.babobka.nodemasterserver.slave;

import lombok.NonNull;
import ru.babobka.nodeserials.NodeData;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.func.Applyer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by 123 on 20.08.2017.
 */
public abstract class AbstractSlave extends Thread {

    private final UUID slaveId;
    private final Map<UUID, NodeRequest> tasks = new HashMap<>();

    AbstractSlave() {
        this.slaveId = UUID.randomUUID();
    }

    void applyToTasks(Applyer<NodeRequest> applyer) {
        synchronized (tasks) {
            for (Map.Entry<UUID, NodeRequest> requestEntry : tasks.entrySet()) {
                applyer.apply(requestEntry.getValue());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractSlave that = (AbstractSlave) o;

        return slaveId.equals(that.slaveId);
    }

    public boolean isNoTasks() {
        synchronized (tasks) {
            return tasks.isEmpty();
        }
    }

    public Map<UUID, NodeRequest> getTasks() {
        synchronized (tasks) {
            return new HashMap<>(tasks);
        }
    }

    void clearTasks() {
        synchronized (tasks) {
            tasks.clear();
        }
    }

    public void removeTask(@NonNull NodeData nodeData) {
        removeTask(nodeData.getId());
    }

    public void removeTask(@NonNull UUID requestId) {
        synchronized (tasks) {
            tasks.remove(requestId);
        }
    }

    void addTask(@NonNull NodeRequest request) {
        synchronized (tasks) {
            tasks.put(request.getId(), request);
        }
    }

    void addTasks(@NonNull List<NodeRequest> requests) {
        synchronized (tasks) {
            for (NodeRequest request : requests) {
                addTask(request);
            }
        }
    }

    boolean hasTask(UUID taskId) {
        synchronized (tasks) {
            for (Map.Entry<UUID, NodeRequest> requestEntry : tasks.entrySet()) {
                if (requestEntry.getValue().getTaskId().equals(taskId)) {
                    return true;
                }
            }
            return false;
        }
    }

    boolean hasRequest(NodeRequest request) {
        synchronized (tasks) {
            return tasks.containsKey(request.getId());
        }
    }


    @Override
    public int hashCode() {
        return slaveId.hashCode();
    }

    @Override
    public String toString() {
        return slaveId.toString();
    }

    public UUID getSlaveId() {
        return slaveId;
    }

    protected abstract void onReceive(NodeResponse response);

    protected abstract void onExit();

}
