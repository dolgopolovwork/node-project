package ru.babobka.nodemasterserver.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.exception.EmptyClusterException;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.MathUtil;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeserials.NodeRequest;

public final class DistributionService {

	private static final int MAX_RETRY = 5;

	private final SimpleLogger logger = Container.getInstance()
			.get(SimpleLogger.class);

	private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

	public void redistribute(Slave slave)
			throws DistributionException, EmptyClusterException {

		if (slave.getRequestMap().size() > 0) {
			if (!slavesStorage.isEmpty()) {
				logger.log("Redistribution");
				Map<String, LinkedList<NodeRequest>> requestsByUri = slave
						.getRequestsGroupedByTask();
				for (Map.Entry<String, LinkedList<NodeRequest>> requestByUriEntry : requestsByUri
						.entrySet()) {
					try {
						broadcastRequests(requestByUriEntry.getKey(),
								requestByUriEntry.getValue(), MAX_RETRY);
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Redistribution failed");
						throw new DistributionException(e);
					}
				}
			} else {
				logger.log(Level.SEVERE,
						"Redistribution failed due to empty cluster");
				throw new EmptyClusterException();
			}
		}

	}

	private void broadcastRequests(String taskName,
			LinkedList<NodeRequest> requests, int maxBroadcastRetry)
			throws IOException, EmptyClusterException, DistributionException {
		NodeRequest[] requestArray = new NodeRequest[requests.size()];
		int i = 0;
		for (NodeRequest request : requests) {
			requestArray[i] = request;
			i++;
		}
		broadcastRequests(taskName, requestArray, 0, maxBroadcastRetry);
	}

	public void broadcastRequests(String taskName, NodeRequest[] requests)
			throws EmptyClusterException, DistributionException {

		broadcastRequests(taskName, requests, 0, MAX_RETRY);
	}

	private void broadcastRequests(String taskName, NodeRequest[] requests,
			int retry, int maxRetry)
			throws EmptyClusterException, DistributionException {
		List<Slave> clientThreads = slavesStorage.getList(taskName);
		if (clientThreads.isEmpty()) {
			throw new EmptyClusterException();
		} else {

			Iterator<Slave> iterator;
			int i = 0;
			try {
				while (i < requests.length) {
					iterator = clientThreads.iterator();
					while (iterator.hasNext() && i < requests.length) {
						iterator.next().sendRequest(requests[i]);
						i++;
					}
				}
			} catch (IOException e) {
				if (retry < maxRetry) {
					logger.log("Broadcast retry " + retry);
					broadcastRequests(taskName, MathUtil.subArray(requests, i),
							retry + 1, maxRetry);
				} else {
					throw new DistributionException(e);
				}
			}

		}

	}

	public void broadcastStopRequests(List<Slave> slaves,
			NodeRequest stopRequest) throws EmptyClusterException {
		if (slaves.isEmpty()) {
			throw new EmptyClusterException();
		} else {
			for (Slave slave : slaves) {
				try {
					slave.sendStopRequest(stopRequest);
				} catch (Exception e) {
					logger.log(Level.SEVERE, e);
				}
			}

		}

	}

}
