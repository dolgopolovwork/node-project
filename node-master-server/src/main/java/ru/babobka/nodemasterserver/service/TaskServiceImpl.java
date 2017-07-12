package ru.babobka.nodemasterserver.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.exception.EmptyClusterException;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.model.ResponsesArray;
import ru.babobka.nodemasterserver.model.Timer;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.task.TaskContext;
import ru.babobka.nodemasterserver.task.TaskPool;
import ru.babobka.nodemasterserver.task.TaskResult;
import ru.babobka.nodemasterserver.task.TaskStartResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.RequestDistributor;
import ru.babobka.subtask.model.SubTask;

public class TaskServiceImpl implements TaskService {

    private TaskPool taskPool = Container.getInstance().get(TaskPool.class);

    private static final String WRONG_ARGUMENTS = "Wrong arguments";

    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    private final ResponseStorage responseStorage = Container.getInstance().get(ResponseStorage.class);

    private final DistributionService distributionService = Container.getInstance().get(DistributionService.class);

    private void startTask(TaskContext taskContext, UUID taskId, Map<String, String> arguments, int maxNodes)
            throws EmptyClusterException, DistributionException {
        int currentClusterSize;
        String taskName = taskContext.getConfig().getName();
        if (isRequestDataIsTooSmall(taskContext.getTask(), arguments)) {
            currentClusterSize = 1;
        } else {
            currentClusterSize = slavesStorage.getClusterSize(taskName);
            if (maxNodes > 0 && currentClusterSize > 0 && maxNodes < currentClusterSize) {
                currentClusterSize = maxNodes;
            }
        }
        responseStorage.create(taskId, new ResponsesArray(currentClusterSize, taskContext, arguments));
        if (currentClusterSize > 0) {
            NodeRequest[] requests = taskContext.getTask().getDistributor().distribute(arguments, currentClusterSize,
                    taskId);
            distributionService.broadcastRequests(taskName, requests);

        } else {
            throw new EmptyClusterException();
        }

    }

    private boolean isRequestDataIsTooSmall(SubTask task, Map<String, String> arguments) {
        NodeRequest request = task.getDistributor().distribute(arguments, 1, UUID.randomUUID())[0];
        return task.isRequestDataTooSmall(request);
    }

    private TaskStartResult startTask(Map<String, String> requestArguments, TaskContext taskContext, UUID taskId,
                                      int maxNodes) {

        RequestDistributor requestDistributor = taskContext.getTask().getDistributor();
        if (requestDistributor.validArguments(requestArguments)) {
            logger.info("Task id is " + taskId);
            try {
                startTask(taskContext, taskId, requestArguments, maxNodes);
                return TaskStartResult.ok(taskId);
            } catch (DistributionException e) {
                logger.error(e);
                try {
                    distributionService.broadcastStopRequests(slavesStorage.getListByTaskId(taskId),
                            NodeRequest.stop(taskId, taskContext.getConfig().getName()));
                } catch (EmptyClusterException e1) {
                    logger.error(e1);
                }
                return TaskStartResult.systemError(taskId, "Can not distribute your request");
            } catch (EmptyClusterException e) {
                logger.error(e);
                return TaskStartResult.systemError(taskId, "Can not distribute due to empty cluster");
            }

        } else {
            logger.error(WRONG_ARGUMENTS);
            return TaskStartResult.failed(taskId, WRONG_ARGUMENTS);
        }

    }

    private Map<String, Serializable> getTaskResult(UUID taskId) throws TimeoutException {
        try {
            ResponsesArray responsesArray = responseStorage.get(taskId);
            if (responsesArray != null) {
                return taskPool.get(responsesArray.getMeta().getTaskName()).getTask().getReducer()
                        .reduce(responsesArray.getResponseList()).map();

            } else {
                logger.error("No such task");
            }
        } catch (TimeoutException e) {
            logger.error("getTaskResult() reaches timeout");
            throw e;

        } catch (Exception e) {
            logger.error(e);
        }
        return new HashMap<>();
    }

    @Override
    public TaskResult cancelTask(UUID taskId) {
        try {
            logger.info("Trying to cancel task " + taskId);
            ResponsesArray responsesArray = responseStorage.get(taskId);
            if (responsesArray != null) {
                distributionService.broadcastStopRequests(slavesStorage.getListByTaskId(taskId),
                        NodeRequest.stop(taskId, responsesArray.getMeta().getTaskName()));
                responseStorage.setStopAllResponses(taskId);
                return new TaskResult("Task " + taskId + " was canceled");
            } else {
                logger.error("No task was found for given task id");
                throw new IllegalArgumentException("No task was found for given task id");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(WRONG_ARGUMENTS, e);
        } catch (EmptyClusterException e) {
            logger.error(e);
            throw new IllegalStateException("Can not cancel task due to empty cluster", e);
        }
    }

    @Override
    public TaskResult getResult(Map<String, String> requestArguments, TaskContext taskContext, int maxNodes)
            throws TimeoutException {
        UUID taskId = UUID.randomUUID();
        try {
            TaskStartResult startResult = startTask(requestArguments, taskContext, taskId, maxNodes);
            if (!startResult.isFailed()) {
                Timer timer = new Timer();
                Map<String, Serializable> resultMap = getTaskResult(startResult.getTaskId());
                if (!resultMap.isEmpty()) {
                    return new TaskResult(timer.getTimePassed(), resultMap);
                } else {
                    throw new IllegalStateException("Can not find result");
                }
            } else if (startResult.isSystemError()) {
                throw new IllegalStateException("System error");
            } else {
                throw new IllegalArgumentException(startResult.getMessage());
            }
        } finally {
            responseStorage.remove(taskId);
        }
    }

}
