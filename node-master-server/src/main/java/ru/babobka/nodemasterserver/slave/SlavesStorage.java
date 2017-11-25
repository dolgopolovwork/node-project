package ru.babobka.nodemasterserver.slave;

import ru.babobka.nodeserials.NodeData;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * Created by dolgopolov.a on 28.07.15.
 */
public class SlavesStorage {

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final List<Slave> slaves = new ArrayList<>();

    public synchronized List<SlaveUser> getCurrentClusterUserList() {
        List<SlaveUser> clusterUserList = new ArrayList<>();
        for (Slave slave : slaves) {
            if (!slave.isInterrupted())
                clusterUserList.add(new SlaveUser(slave));
        }
        return clusterUserList;
    }

    synchronized void remove(Slave slave) {
        logger.info("Remove slave " + slave);
        slaves.remove(slave);
    }

    synchronized void add(Slave slave) {
        logger.info("Add new slave " + slave);
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

    public synchronized List<Slave> getListByTaskId(NodeData nodeData) {
        if (nodeData == null) {
            throw new IllegalArgumentException("nodeData is null");
        }
        return getListByTaskId(nodeData.getTaskId());
    }

    public synchronized List<Slave> getListByTaskId(UUID taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is null");
        }
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
            logger.error(e);
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
        if (!isEmpty()) {
            interruptAll();
            slaves.clear();
        }
    }

    public synchronized boolean isEmpty() {
        return slaves.isEmpty();
    }

}
