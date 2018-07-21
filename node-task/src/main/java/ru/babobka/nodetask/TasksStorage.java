package ru.babobka.nodetask;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.StoppedTasks;
import ru.babobka.nodetask.model.SubTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dolgopolov.a on 29.09.15.
 */
public class TasksStorage {

    private final Map<UUID, HashMap<UUID, SubTask>> runningTasks = new HashMap<>();
    private final StoppedTasks stoppedTasks = new StoppedTasks();

    public synchronized void put(NodeRequest request, SubTask subTask) {
        if (!runningTasks.containsKey(request.getTaskId())) {
            runningTasks.put(request.getTaskId(), new HashMap<>());
        }
        runningTasks.get(request.getTaskId()).put(request.getId(), subTask);
    }

    public synchronized boolean exists(UUID taskId) {
        return runningTasks.containsKey(taskId);
    }

    public synchronized void removeRequest(NodeRequest request) {
        Map<UUID, SubTask> localTaskMap = runningTasks.get(request.getTaskId());
        if (localTaskMap != null) {
            localTaskMap.remove(request.getId());
            if (localTaskMap.isEmpty()) {
                runningTasks.remove(request.getTaskId());
            }
        }
    }

    public synchronized void stopTask(NodeRequest request) {
        stopTask(request.getTaskId(), request.getTimeStamp());
    }

    public synchronized void stopAllTheTasks() {
        for (Map.Entry<UUID, HashMap<UUID, SubTask>> taskEntry : runningTasks.entrySet()) {
            stopTaskNoRemove(taskEntry.getKey(), System.currentTimeMillis());
        }
        runningTasks.clear();
    }

    synchronized void stopTaskNoRemove(UUID taskId, long timeStamp) {
        stoppedTasks.add(taskId, timeStamp);
        Map<UUID, SubTask> localTaskMap = runningTasks.get(taskId);
        if (localTaskMap != null) {
            for (Map.Entry<UUID, SubTask> task : localTaskMap.entrySet()) {
                task.getValue().stopProcess();
            }
        }
    }

    synchronized void stopTask(UUID taskId, long timeStamp) {
        System.err.println("stopTask(UUID taskId, long timeStamp) " + taskId);
        stopTaskNoRemove(taskId, timeStamp);
        runningTasks.remove(taskId);
    }

    public synchronized boolean wasStopped(NodeRequest request) {
        return stoppedTasks.wasStopped(request);
    }

    public synchronized void clear() {
        runningTasks.clear();
        stoppedTasks.clear();
    }
}
