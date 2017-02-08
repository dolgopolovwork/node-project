package ru.babobka.nodemasterserver.model;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodemasterserver.exception.EmptyClusterException;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.task.TaskContext;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public final class ResponsesArray {

	private final AtomicInteger size;

	private final int maxSize;

	private final ResponsesArrayMeta meta;

	private static final String TASK = "Task";

	private static final int HOUR_MILLIS = 1000 * 60 * 60;

	private final AtomicReferenceArray<NodeResponse> responseArray;

	private final DistributionService distributionService;

	private final Object lock = new Object();

	private final TaskContext taskContext;

	private final SimpleLogger logger;

	private final SlavesStorage slavesStorage;

	public ResponsesArray(int maxSize, TaskContext taskContext, Map<String, String> params) {
		this.maxSize = maxSize;
		this.taskContext = taskContext;
		this.responseArray = new AtomicReferenceArray<>(maxSize);
		this.meta = new ResponsesArrayMeta(taskContext.getConfig().getName(), params, System.currentTimeMillis());
		size = new AtomicInteger(0);
		distributionService = Container.getInstance().get(DistributionService.class);
		logger = Container.getInstance().get(SimpleLogger.class);
		slavesStorage = Container.getInstance().get(SlavesStorage.class);
	}

	private ResponsesArray() {
		this.maxSize = 1;
		this.responseArray = new AtomicReferenceArray<>(maxSize);
		this.taskContext = null;
		this.meta = null;
		size = null;
		distributionService = null;
		logger = null;
		slavesStorage = null;
	}

	static ResponsesArray dummyResponsesArray() {
		return new ResponsesArray();
	}

	public boolean isComplete() {
		synchronized (lock) {
			for (int i = 0; i < responseArray.length(); i++) {
				if (responseArray.get(i) == null) {
					return false;
				}
			}
			return true;
		}
	}

	public int getMaxSize() {
		return maxSize;
	}

	public boolean add(NodeResponse response) {
		synchronized (lock) {
			int corruptedResponseCount = 0;
			if (size.intValue() >= responseArray.length()) {
				return false;
			} else {
				for (int i = 0; i < responseArray.length(); i++) {
					if (responseArray.get(i) == null) {
						responseArray.set(i, response);
						if (response.isStopped()) {
							corruptedResponseCount++;
						}
						size.incrementAndGet();
						if (size.intValue() == responseArray.length()) {
							lock.notifyAll();
							if (corruptedResponseCount == size.intValue()) {
								logger.log(TASK + " " + response.getTaskId() + " was canceled");
							} else {
								logger.log(TASK + " " + response.getTaskId() + " is ready ");
							}
						} else if (taskContext.getConfig().isRaceStyle()
								&& taskContext.getTask().getReducer().isValidResponse(response)) {
							List<Slave> slaves = slavesStorage.getListByTaskId(response.getTaskId());
							try {
								if (!slaves.isEmpty()) {
									logger.log("Cancel all requests for task id " + response.getTaskId());
									distributionService.broadcastStopRequests(slaves,
											new NodeRequest(response.getTaskId(), true, response.getTaskName()));
								}
							} catch (EmptyClusterException e) {
								logger.log(e);
							}

						}

						break;

					}
				}
				return true;
			}
		}
	}

	public void fill(NodeResponse response) {
		synchronized (lock) {
			if (size.intValue() <= responseArray.length()) {
				for (int i = 0; i < responseArray.length(); i++) {
					if (responseArray.get(i) == null) {
						responseArray.set(i, response);
						size.incrementAndGet();
						if (size.intValue() == responseArray.length()) {
							lock.notifyAll();
							logger.log(TASK + " " + response.getTaskId() + " is ready due to filling");
							break;
						}
					}
				}
			}
		}

	}

	public List<NodeResponse> getResponseList() throws TimeoutException {
		synchronized (lock) {
			List<NodeResponse> responses = new LinkedList<>();
			boolean completed = false;
			try {
				while (size.intValue() != responseArray.length()) {
					lock.wait(HOUR_MILLIS);
				}
				for (int i = 0; i < responseArray.length(); i++) {
					responses.add(responseArray.get(i));
				}
				completed = true;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.log(e);
			}
			if (!completed && !Thread.currentThread().isInterrupted()) {
				throw new TimeoutException();
			}
			return responses;
		}

	}

	public ResponsesArrayMeta getMeta() {
		return meta;
	}

	@Override
	public String toString() {
		return meta.getTaskName();
	}

}