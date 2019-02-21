package ru.babobka.nodemasterserver.service;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.slave.AbstractNetworkSlave;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DistributionService {

    private static final int MAX_RETRY = 10;
    private static final Logger logger = Logger.getLogger(DistributionService.class);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    public void redistribute(@NonNull AbstractNetworkSlave slave) throws DistributionException {
        if (slave.isNoTasks()) {
            return;
        }
        logger.debug("redistribution");
        Map<String, List<NodeRequest>> groupedTasks = slave.getRequestsGroupedByTasks();
        for (Map.Entry<String, List<NodeRequest>> requestByUriEntry : groupedTasks.entrySet()) {
            try {
                broadcastRequests(requestByUriEntry.getKey(), requestByUriEntry.getValue());
            } catch (Exception e) {
                logger.error("redistribution failed");
                throw new DistributionException(e);
            }
        }
    }

    void broadcastRequests(String taskFactoryName, List<NodeRequest> requests)
            throws DistributionException {
        broadcastRequests(taskFactoryName, requests, 0, MAX_RETRY);
    }

    void broadcastRequests(@NonNull String taskName, @NonNull List<NodeRequest> requests, int retry, int maxRetry)
            throws DistributionException {
        if (maxRetry < 0) {
            throw new IllegalArgumentException("maxRetry cannot be negative");
        }
        logger.debug("requests to broadcast " + requests);
        int lastRequestId = -1;
        try {
            List<Slave> slaves = slavesStorage.getList(taskName);
            if (slaves.isEmpty()) {
                throw new IOException("cluster is empty");
            }
            logger.debug("slaves to broadcast " + slaves);
            for (NodeRequest request : requests) {
                lastRequestId++;
                slaves.get(lastRequestId % slaves.size()).executeTask(request);
            }
        } catch (IOException e) {
            if (retry < maxRetry) {
                waitForGoodTimes();
                logger.debug("broadcast retry " + retry);
                logger.error("exception thrown", e);
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
            logger.error("exception thrown", e);
        }
    }

    public boolean broadcastStopRequests(List<Slave> slaves, @NonNull UUID taskId) {
        if (slaves == null || slaves.isEmpty()) {
            logger.debug("no slaves to broadcast");
            return false;
        }
        for (Slave slave : slaves) {
            try {
                slave.stopTask(taskId);
            } catch (IOException e) {
                logger.error("exception thrown", e);
            }
        }
        return true;
    }
}
