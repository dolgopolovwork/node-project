package ru.babobka.nodemasterserver.slave;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodeserials.NodeData;

import java.io.IOException;
import java.util.*;


/**
 * Created by dolgopolov.a on 28.07.15.
 */
public class SlavesStorage {

    private static final Logger logger = Logger.getLogger(SlavesStorage.class);
    private final List<Slave> slaves = new ArrayList<>();
    private final UUID storageId = UUID.randomUUID();
    private boolean closed;

    public SlavesStorage() {
        logger.debug("slave storage " + storageId + " was created");
    }

    public synchronized void closeStorage() {
        this.closed = true;
    }

    public synchronized void remove(Slave slave) {
        logger.info("remove slave " + slave + " from storage " + storageId);
        slaves.remove(slave);
    }

    public synchronized boolean add(Slave slave) {
        if (!isClosed()) {
            logger.info("add new slave " + slave + " to slave storage " + storageId);
            slaves.add(slave);
            return true;
        } else {
            logger.info("slave " + slave + " was not added to slave storage " + storageId + " due to closed storage status");
            return false;
        }
    }

    public synchronized List<Slave> getFullList() {
        List<Slave> fullSlaveList = new ArrayList<>(this.slaves.size());
        for (Slave slave : slaves) {
            if (!slave.isInterrupted()) {
                fullSlaveList.add(slave);
            }
        }
        return fullSlaveList;
    }

    public synchronized List<Slave> getList(String taskName) {
        return getList(taskName, slaves.size());
    }

    public synchronized List<Slave> getList(String taskName, int maxSlaves) {
        if (maxSlaves < 1) {
            return new ArrayList<>();
        }
        Map<UUID, Long> slaveUsageMap = new HashMap<>();
        List<Slave> groupedSlaves = new ArrayList<>();
        for (Slave slave : getFullList()) {
            if (!slave.isInterrupted() && slave.taskIsAvailable(taskName)) {
                slaveUsageMap.put(slave.getSlaveId(), slave.getLastSendRequestTime());
                groupedSlaves.add(slave);
            }
        }
        Collections.sort(groupedSlaves, (slave1, slave2) -> slaveUsageMap.get(slave1.getSlaveId()).compareTo(slaveUsageMap.get(slave2.getSlaveId())));
        return groupedSlaves.subList(0, Math.min(maxSlaves, groupedSlaves.size()));
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
            logger.error("exception thrown", e);
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
        logger.debug("clear storage " + storageId);
        if (!isEmpty()) {
            interruptAll();
            slaves.clear();
        }
    }

    private synchronized boolean isClosed() {
        return closed;
    }

    public synchronized boolean isEmpty() {
        return slaves.isEmpty();
    }

}
