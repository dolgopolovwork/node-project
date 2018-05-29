package ru.babobka.nodemasterserver.slave;

import lombok.NonNull;
import ru.babobka.nodeserials.NodeData;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * Created by dolgopolov.a on 28.07.15.
 */
public class SlavesStorage {

    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final List<Slave> slaves = new ArrayList<>();
    private final UUID storageId = UUID.randomUUID();

    public SlavesStorage() {
        nodeLogger.debug("slave storage " + storageId + " was created");
    }

    synchronized void remove(Slave slave) {
        nodeLogger.info("remove slave " + slave + " from storage " + storageId);
        slaves.remove(slave);
    }

    synchronized void add(Slave slave) {
        nodeLogger.info("add new slave " + slave + " to storage " + storageId);
        slaves.add(slave);
    }

    public synchronized List<Slave> getFullList() {
        List<Slave> fullSlaveList = new ArrayList<>(this.slaves.size());
        for (Slave slave : slaves) {
            if (!slave.isInterrupted())
                fullSlaveList.add(slave);
        }
        Collections.shuffle(fullSlaveList);
        return fullSlaveList;
    }

    public synchronized List<Slave> getList(String taskName) {
        return getList(taskName, slaves.size());
    }

    public synchronized List<Slave> getList(String taskName, int maxSlaves) {
        if (maxSlaves < 1) {
            return new ArrayList<>();
        }
        List<Slave> groupedSlaves = new ArrayList<>();
        for (Slave slave : getFullList()) {
            if (!slave.isInterrupted() && slave.taskIsAvailable(taskName)) {
                groupedSlaves.add(slave);
                if (groupedSlaves.size() == maxSlaves) {
                    break;
                }
            }
        }
        return groupedSlaves;
    }

    public synchronized List<Slave> getListByTaskId(@NonNull NodeData nodeData) {
        return getListByTaskId(nodeData.getTaskId());
    }

    public synchronized List<Slave> getListByTaskId(@NonNull UUID taskId) {
        List<Slave> groupedSlaves = new ArrayList<>();
        for (Slave slave : getFullList()) {
            if (!slave.isInterrupted() && slave.hasTask(taskId)) {
                groupedSlaves.add(slave);
            }
        }
        return groupedSlaves;
    }

    public synchronized void heartBeatAllSlaves() {
        for (Slave slave : getFullList()) {
            if (!slave.isInterrupted() && !Thread.currentThread().isInterrupted()) {
                sendHeartBeat(slave);
            }
        }
    }

    void sendHeartBeat(Slave slave) {
        try {
            slave.sendHeartBeating();
        } catch (IOException e) {
            nodeLogger.error(e);
        }
    }

    public synchronized int getClusterSize() {
        return slaves.size();
    }

    public synchronized int getClusterSize(String taskName) {
        int counter = 0;
        for (Slave slave : slaves) {
            if (!slave.isInterrupted() && slave.taskIsAvailable(taskName)) {
                counter++;
            }
        }
        return counter;
    }

    synchronized void interruptAll() {
        for (Slave slave : slaves) {
            slave.interrupt();
        }
    }

    public synchronized void clear() {
        nodeLogger.debug("clear storage " + storageId);
        if (!isEmpty()) {
            interruptAll();
            slaves.clear();
        }
    }

    public synchronized boolean isEmpty() {
        return slaves.isEmpty();
    }

}
