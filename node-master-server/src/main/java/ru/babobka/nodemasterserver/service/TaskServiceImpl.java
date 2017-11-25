package ru.babobka.nodemasterserver.service;

import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.model.Responses;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodemasterserver.task.TaskStartResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodetask.model.DataValidators;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.time.Timer;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class TaskServiceImpl implements TaskService {

    private static final String WRONG_ARGUMENTS = "Wrong arguments";
    private final TaskPool taskPool = Container.getInstance().get("masterServerTaskPool");
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final ResponseStorage responseStorage = Container.getInstance().get(ResponseStorage.class);
    private final DistributionService distributionService = Container.getInstance().get(DistributionService.class);

    @Override
    public boolean cancelTask(UUID taskId) throws TaskExecutionException {
        if (taskId == null)
            throw new IllegalArgumentException("taskId is null");
        try {
            logger.info("Trying to cancel task " + taskId);
            Responses responses = responseStorage.get(taskId);
            if (responses == null)
                return false;
            responseStorage.setStopAllResponses(taskId);
            distributionService.broadcastStopRequests(slavesStorage.getListByTaskId(taskId), taskId);
        } catch (DistributionException | RuntimeException e) {
            throw new TaskExecutionException("Can not cancel task", e);
        }
        return true;
    }

    @Override
    public TaskExecutionResult executeTask(NodeRequest request, int maxNodes)
            throws TaskExecutionException {
        try {
            SubTask task = taskPool.get(request.getTaskName());
            TaskStartResult startResult = startTask(request, task, maxNodes);
            if (startResult.isSystemError()) {
                throw new TaskExecutionException("System error");
            } else if (startResult.isFailed()) {
                throw new TaskExecutionException(startResult.getMessage());
            }
            Timer timer = new Timer();
            Responses responses = responseStorage.get(request.getTaskId());
            if (responses == null)
                throw new TaskExecutionException("No such task with given id " + request.getTaskId());
            List<NodeResponse> responseList = responses.getResponseList();
            if (responses.isStopped()) {
                return TaskExecutionResult.stopped();
            }
            Map<String, Serializable> resultMap = task.getReducer().reduce(responseList).map();
            logger.info("Got responses " + responses);
            return TaskExecutionResult.normal(timer, resultMap);
        } catch (IOException | ReducingException | TimeoutException | RuntimeException e) {
            throw new TaskExecutionException(e);
        } finally {
            responseStorage.remove(request.getTaskId());
        }
    }

    @Override
    public TaskExecutionResult executeTask(NodeRequest request) throws TaskExecutionException {
        return executeTask(request, 0);
    }

    void broadcastTask(NodeRequest request, SubTask task, int maxNodes)
            throws DistributionException {
        if (maxNodes < 0)
            throw new IllegalArgumentException("maxNodes must be at least 0");
        UUID taskId = request.getTaskId();
        int clusterSize = 1;
        if (!task.isRequestDataTooSmall(request)) {
            int actualClusterSize = slavesStorage.getClusterSize(request.getTaskName());
            if (maxNodes == 0) {
                clusterSize = actualClusterSize;
            } else {
                clusterSize = Math.min(maxNodes, actualClusterSize);
            }
        }
        if (clusterSize <= 0) {
            throw new DistributionException("cluster size is " + clusterSize);
        }
        logger.info("Cluster size is " + clusterSize);
        responseStorage.create(taskId, new Responses(clusterSize, task, request.getData()));
        List<NodeRequest> requests = task.getDistributor().distribute(request, clusterSize);
        logger.info("Requests to distribute " + requests);
        distributionService.broadcastRequests(request.getTaskName(), requests);
    }


    private TaskStartResult startTask(NodeRequest request, SubTask task, int maxNodes) {
        UUID taskId = request.getTaskId();
        DataValidators dataValidators = task.getDataValidators();
        if (!dataValidators.isValidRequest(request)) {
            logger.error(WRONG_ARGUMENTS);
            return TaskStartResult.failed(taskId, WRONG_ARGUMENTS);
        }
        logger.info("Started task id is " + taskId);
        try {
            broadcastTask(request, task, maxNodes);
            return TaskStartResult.ok(taskId);
        } catch (DistributionException e) {
            logger.error(e);
            try {
                List<Slave> slaves = slavesStorage.getListByTaskId(taskId);
                distributionService.broadcastStopRequests(slaves, taskId);
            } catch (DistributionException e1) {
                logger.error(e1);
            }
            return TaskStartResult.systemError(taskId, "Can not distribute task data");
        }
    }

}
