package ru.babobka.nodemasterserver.service;

import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.slave.AbstractNetworkSlave;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//TODO напиши на это тест
public class DistributionService {

    private static final int MAX_RETRY = 5;

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    public void redistribute(AbstractNetworkSlave slave) throws DistributionException {
        if (slave == null) {
            throw new IllegalArgumentException("slave is null");
        } else if (slave.isNoTasks()) {
            return;
        }
        logger.info("Redistribution");
        Map<String, List<NodeRequest>> groupedTasks = slave.getRequestsGroupedByTasks();
        for (Map.Entry<String, List<NodeRequest>> requestByUriEntry : groupedTasks.entrySet()) {
            try {
                broadcastRequests(requestByUriEntry.getKey(), requestByUriEntry.getValue());
            } catch (Exception e) {
                logger.error("Redistribution failed");
                throw new DistributionException(e);
            }
        }
    }

    void broadcastRequests(String taskFactoryName, List<NodeRequest> requests)
            throws DistributionException {
        broadcastRequests(taskFactoryName, requests, 0, MAX_RETRY);
    }

    void broadcastRequests(String taskFactoryName, List<NodeRequest> requests, int retry, int maxRetry)
            throws DistributionException {
        if (maxRetry < 0) {
            throw new IllegalArgumentException("maxRetry can not be negative");
        } else if (requests == null) {
            throw new IllegalArgumentException("requests is null");
        }
        List<Slave> slaves = slavesStorage.getList(taskFactoryName);
        if (slaves.isEmpty()) {
            throw new DistributionException("cluster is empty");
        }
        int lastRequestId = 0;
        try {
            for (NodeRequest request : requests) {
                lastRequestId++;
                slaves.get(lastRequestId % slaves.size()).executeTask(request);
            }
        } catch (IOException e) {
            if (retry < maxRetry) {
                logger.info("Broadcast retry " + retry);
                broadcastRequests(taskFactoryName, requests.subList(lastRequestId, requests.size()), retry + 1, maxRetry);
            } else {
                throw new DistributionException(e);
            }
        }
    }

    public void broadcastStopRequests(List<Slave> slaves, UUID taskId) throws DistributionException {
        if (slaves == null || slaves.isEmpty()) {
            throw new DistributionException("cluster is empty");
        }
        for (Slave slave : slaves) {
            try {
                slave.stopTask(taskId);
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
}
