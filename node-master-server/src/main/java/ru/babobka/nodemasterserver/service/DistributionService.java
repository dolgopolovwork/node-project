package ru.babobka.nodemasterserver.service;

import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.slave.AbstractNetworkSlave;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DistributionService {

    private static final int MAX_RETRY = 10;

    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);

    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    public void redistribute(AbstractNetworkSlave slave) throws DistributionException {
        if (slave == null) {
            throw new IllegalArgumentException("slave is null");
        } else if (slave.isNoTasks()) {
            return;
        }
        nodeLogger.debug("redistribution");
        Map<String, List<NodeRequest>> groupedTasks = slave.getRequestsGroupedByTasks();
        for (Map.Entry<String, List<NodeRequest>> requestByUriEntry : groupedTasks.entrySet()) {
            try {
                broadcastRequests(requestByUriEntry.getKey(), requestByUriEntry.getValue());
            } catch (Exception e) {
                nodeLogger.error("redistribution failed");
                throw new DistributionException(e);
            }
        }
    }

    void broadcastRequests(String taskFactoryName, List<NodeRequest> requests)
            throws DistributionException {
        broadcastRequests(taskFactoryName, requests, 0, MAX_RETRY);
    }

    void broadcastRequests(String taskName, List<NodeRequest> requests, int retry, int maxRetry)
            throws DistributionException {
        if (maxRetry < 0) {
            throw new IllegalArgumentException("maxRetry cannot be negative");
        } else if (requests == null) {
            throw new IllegalArgumentException("requests is null");
        }
        nodeLogger.debug("requests to broadcast " + requests);
        int lastRequestId = -1;
        try {
            List<Slave> slaves = slavesStorage.getList(taskName);
            if (slaves.isEmpty()) {
                throw new IOException("cluster is empty");
            }
            nodeLogger.debug("slaves to broadcast " + slaves);
            for (NodeRequest request : requests) {
                lastRequestId++;
                slaves.get(lastRequestId % slaves.size()).executeTask(request);
            }
        } catch (IOException e) {
            if (retry < maxRetry) {
                waitForGoodTimes();
                nodeLogger.debug("broadcast retry " + retry);
                nodeLogger.error(e);
                broadcastRequests(taskName, requests.subList(Math.max(lastRequestId, 0), requests.size()), retry + 1, maxRetry);
            } else {
                throw new DistributionException(e);
            }
        }
    }

    private void waitForGoodTimes() {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            nodeLogger.error(e);
        }
    }

    public boolean broadcastStopRequests(List<Slave> slaves, UUID taskId) {
        if (slaves == null || slaves.isEmpty()) {
            nodeLogger.debug("no slaves to broadcast");
            return false;
        }
        for (Slave slave : slaves) {
            try {
                slave.stopTask(taskId);
            } catch (IOException e) {
                nodeLogger.error(e);
            }
        }
        return true;
    }
}
