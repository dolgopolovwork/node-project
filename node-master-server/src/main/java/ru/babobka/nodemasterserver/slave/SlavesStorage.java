package ru.babobka.nodemasterserver.slave;

import ru.babobka.nodeserials.NodeRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by dolgopolov.a on 28.07.15.
 */
public class SlavesStorage {

    private final AtomicReferenceArray<Slave> threads;

    private final AtomicInteger size = new AtomicInteger(0);

    public SlavesStorage(int maxSize) {
        this.threads = new AtomicReferenceArray<>(maxSize);
    }

    public synchronized List<SlaveUser> getCurrentClusterUserList() {
        List<SlaveUser> clusterUserList = new ArrayList<>();
        Slave slave;
        for (int i = 0; i < threads.length(); i++) {
            if ((slave = threads.get(i)) != null) {
                clusterUserList
                        .add(new SlaveUser(slave));
            }
        }

        return clusterUserList;
    }

    synchronized boolean remove(Slave slave) {
        if (slave != null) {
            for (int i = 0; i < threads.length(); i++) {
                if (threads.get(i) == slave) {
                    threads.set(i, null);
                    size.decrementAndGet();
                    return true;

                }

            }
        }
        return false;
    }

    synchronized boolean add(Slave slave) {
        if (slave != null && size.intValue() != threads.length()) {
            for (int i = 0; i < threads.length(); i++) {
                if (threads.get(i) == null) {
                    threads.set(i, slave);
                    size.incrementAndGet();
                    return true;

                }

            }
        }
        return false;
    }

    public synchronized List<Slave> getFullList() {
        ArrayList<Slave> slaveThreadList = new ArrayList<>();
        for (int i = 0; i < threads.length(); i++) {
            Slave st = threads.get(i);
            if (st != null) {
                slaveThreadList.add(st);
            }
        }
        Collections.shuffle(slaveThreadList);
        return slaveThreadList;
    }

    public synchronized List<Slave> getList(String taskName) {
        return getList(taskName, -1);
    }

    public synchronized List<Slave> getList(String taskName, int maxThreads) {
        List<Slave> slaveThreadList = new ArrayList<>();
        Slave st;
        for (int i = 0; i < threads.length(); i++) {
            st = threads.get(i);
            if (st != null && st.getAvailableTasksSet() != null && st.getAvailableTasksSet().contains(taskName)) {
                slaveThreadList.add(st);
            }
        }
        Collections.shuffle(slaveThreadList);
        if (maxThreads != -1 && maxThreads < slaveThreadList.size()) {
            return slaveThreadList.subList(0, maxThreads);
        }
        return slaveThreadList;
    }

    public synchronized List<Slave> getListByTaskId(UUID taskId) {
        List<Slave> slaveThreadList = new ArrayList<>();
        Slave st;
        for (int i = 0; i < threads.length(); i++) {
            st = threads.get(i);
            if (st != null && !st.getRequestMap().isEmpty()) {
                for (Map.Entry<UUID, NodeRequest> requestEntry : st.getRequestMap().entrySet()) {
                    if (requestEntry.getValue().getTaskId().equals(taskId)) {
                        slaveThreadList.add(st);
                        break;
                    }
                }
            }

        }
        Collections.shuffle(slaveThreadList);
        return slaveThreadList;
    }

    public int getClusterSize() {
        return size.intValue();
    }

    public synchronized int getClusterSize(String taskName) {
        int counter = 0;
        Slave st;
        for (int i = 0; i < threads.length(); i++) {
            st = threads.get(i);
            if (st != null && st.getAvailableTasksSet().contains(taskName)) {
                counter++;
            }
        }
        return counter;
    }

    private synchronized void interruptAll() {

        List<Slave> clientThreadsList = getFullList();
        for (Slave st : clientThreadsList) {
            st.interrupt();
        }
    }

    public synchronized void clear() {

        if (!isEmpty()) {
            interruptAll();
            for (int i = 0; i < threads.length(); i++) {
                threads.set(i, null);
            }
            size.set(0);
        }

    }

    public boolean isEmpty() {
        return size.intValue() == 0;
    }

}
