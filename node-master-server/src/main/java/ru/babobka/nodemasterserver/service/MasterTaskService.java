package ru.babobka.nodemasterserver.service;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodemasterserver.mapper.ResponsesMapper;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.model.Responses;
import ru.babobka.nodebusiness.monitoring.TaskMonitoringService;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodetask.exception.TaskExecutionException;
import ru.babobka.nodetask.model.DataValidators;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodetask.service.TaskExecutionResult;
import ru.babobka.nodetask.service.TaskService;
import ru.babobka.nodetask.service.TaskStartResult;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.Callback;
import ru.babobka.nodeutils.time.Timer;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class MasterTaskService implements TaskService {

    private static final Logger logger = Logger.getLogger(MasterTaskService.class);
    private static final int MAX_ATTEMPTS = 5;
    private final TaskPool taskPool = Container.getInstance().get(MasterServerKey.MASTER_SERVER_TASK_POOL);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final ResponseStorage responseStorage = Container.getInstance().get(ResponseStorage.class);
    private final DistributionService distributionService = Container.getInstance().get(DistributionService.class);
    private final TaskMonitoringService taskMonitoringService = Container.getInstance().get(TaskMonitoringService.class);
    private final ResponsesMapper responsesMapper = Container.getInstance().get(ResponsesMapper.class);

    @Override
    public void cancelTask(@NonNull UUID taskId,
                           @NonNull Callback<Boolean> onTaskCanceledCallback,
                           @NonNull Callback<TaskExecutionException> onError) {
        try {
            logger.debug("trying to cancel task " + taskId);
            Responses responses = responseStorage.get(taskId);
            if (responses == null) {
                logger.debug("no responses were found. cannot cancel.");
                onTaskCanceledCallback.callback(false);
                return;
            }
            responseStorage.setStopAllResponses(taskId);
            taskMonitoringService.incrementCanceledTasksCount();
            onTaskCanceledCallback.callback(distributionService.broadcastStopRequests(slavesStorage.getListByTaskId(taskId), taskId));
        } catch (RuntimeException e) {
            onError.callback(new TaskExecutionException("cannot cancel task", ResponseStatus.SYSTEM_ERROR, e));
        }
    }

    @Override
    public void executeTask(NodeRequest request, int maxNodes,
                            Callback<TaskExecutionResult> onTaskExecutedCallback,
                            Callback<TaskExecutionException> onError) {
        try {
            taskMonitoringService.incrementStartedTasksCount();
            SubTask task = taskPool.get(request.getTaskName());
            TaskStartResult startResult = startTask(request, task, maxNodes);
            if (startResult.isSystemError() || startResult.isValidationError()) {
                taskMonitoringService.incrementFailedTasksCount();
                ResponseStatus taskExecutionStatus;
                if (startResult.isSystemError()) {
                    taskExecutionStatus = ResponseStatus.SYSTEM_ERROR;
                } else {
                    taskExecutionStatus = ResponseStatus.VALIDATION_ERROR;
                }
                onError.callback(new TaskExecutionException(startResult.getMessage(), taskExecutionStatus));
                return;
            }
            Timer timer = new Timer();
            Responses responses = responseStorage.get(request.getTaskId());
            if (responses == null) {
                taskMonitoringService.incrementFailedTasksCount();
                onError.callback(new TaskExecutionException("no such task with given id " + request.getTaskId(), ResponseStatus.SYSTEM_ERROR));
                return;
            }
            TaskExecutionResult result = responsesMapper.map(responses, timer, task);
            taskMonitoringService.incrementExecutedTasksCount();
            onTaskExecutedCallback.callback(result);
        } catch (TaskExecutionException e) {
            taskMonitoringService.incrementFailedTasksCount();
            onError.callback(e);
        } catch (IOException | ReducingException | TimeoutException | RuntimeException e) {
            taskMonitoringService.incrementFailedTasksCount();
            onError.callback(new TaskExecutionException(ResponseStatus.SYSTEM_ERROR, e));
        } finally {
            responseStorage.remove(request.getTaskId());
        }
    }

    @Override
    public void executeTask(NodeRequest request,
                            Callback<TaskExecutionResult> onTaskExecutedCallback,
                            Callback<TaskExecutionException> onError) {
        executeTask(request, 0, onTaskExecutedCallback, onError);
    }

    void broadcastTask(NodeRequest request, SubTask task, int maxNodes) throws DistributionException, TaskExecutionException {
        broadcastTask(request, task, maxNodes, 0);
    }

    private void broadcastTask(NodeRequest request, SubTask task, int maxNodes, int attempt)
            throws DistributionException, TaskExecutionException {
        if (maxNodes < 0)
            throw new IllegalArgumentException("maxNodes must be at least 0");
        UUID taskId = request.getTaskId();
        int clusterSize = getClusterSize(request, task, maxNodes);
        if (clusterSize <= 0) {
            logger.debug("rebroadcast attempt " + attempt);
            if (attempt == MAX_ATTEMPTS) {
                throw new TaskExecutionException("cannot broadcast no more. system reached its max retry attempt.", ResponseStatus.NO_NODES);
            }
            waitForGoodTimes();
            broadcastTask(request, task, maxNodes, attempt + 1);
            return;
        }
        logger.debug("involved nodes " + clusterSize);
        responseStorage.create(taskId, new Responses(clusterSize, task));
        List<NodeRequest> requests = task.getDistributor().distribute(request, clusterSize);
        logger.debug("requests to distribute " + requests);
        distributionService.broadcastRequests(request.getTaskName(), requests);
    }

    private void waitForGoodTimes() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException expected) {
            //that's ok
        }
    }

    private int getClusterSize(NodeRequest request, SubTask task, int maxNodes) {
        int actualClusterSize = slavesStorage.getClusterSize(request.getTaskName());
        if (actualClusterSize == 0) {
            return 0;
        } else if (task.isSingleNodeTask(request))
            return 1;
        return maxNodes == 0 ? actualClusterSize : Math.min(maxNodes, actualClusterSize);
    }

    TaskStartResult startTask(NodeRequest request, SubTask task, int maxNodes) throws TaskExecutionException {
        UUID taskId = request.getTaskId();
        DataValidators dataValidators = task.getDataValidators();
        if (!dataValidators.isValidRequest(request)) {
            return TaskStartResult.validationError(taskId, "wrong arguments");
        } else if (task.isRequestDataTooBig(request)) {
            return TaskStartResult.validationError(taskId, "too big arguments");
        }
        logger.debug("started task id is " + taskId);
        try {
            broadcastTask(request, task, maxNodes);
            return TaskStartResult.ok(taskId);
        } catch (DistributionException e) {
            logger.error("exception thrown", e);
            distributionService.broadcastStopRequests(slavesStorage.getListByTaskId(taskId), taskId);
            return TaskStartResult.systemError(taskId, e.getMessage());
        }
    }
}
