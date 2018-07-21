package ru.babobka.nodemasterserver.service;

import lombok.NonNull;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodemasterserver.mapper.ResponsesMapper;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.model.Responses;
import ru.babobka.nodemasterserver.monitoring.TaskMonitoringService;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodemasterserver.task.TaskStartResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodetask.model.DataValidators;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.time.Timer;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class TaskServiceImpl implements TaskService {

    private static final int MAX_ATTEMPTS = 5;
    private final TaskPool taskPool = Container.getInstance().get(MasterServerKey.MASTER_SERVER_TASK_POOL);
    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final ResponseStorage responseStorage = Container.getInstance().get(ResponseStorage.class);
    private final DistributionService distributionService = Container.getInstance().get(DistributionService.class);
    private final TaskMonitoringService taskMonitoringService = Container.getInstance().get(TaskMonitoringService.class);
    private final ResponsesMapper responsesMapper = Container.getInstance().get(ResponsesMapper.class);

    @Override
    public boolean cancelTask(@NonNull UUID taskId) throws TaskExecutionException {
        try {
            nodeLogger.debug("trying to cancel task " + taskId);
            Responses responses = responseStorage.get(taskId);
            if (responses == null) {
                nodeLogger.debug("no responses were found. cannot cancel.");
                return false;
            }
            responseStorage.setStopAllResponses(taskId);
            taskMonitoringService.incrementCanceledTasksCount();
            return distributionService.broadcastStopRequests(slavesStorage.getListByTaskId(taskId), taskId);
        } catch (RuntimeException e) {
            throw new TaskExecutionException("cannot cancel task", e);
        }
    }

    @Override
    public TaskExecutionResult executeTask(NodeRequest request, int maxNodes)
            throws TaskExecutionException {
        try {
            taskMonitoringService.incrementStartedTasksCount();
            SubTask task = taskPool.get(request.getTaskName());
            TaskStartResult startResult = startTask(request, task, maxNodes);
            if (startResult.isSystemError() || startResult.isFailed()) {
                taskMonitoringService.incrementFailedTasksCount();
                throw new TaskExecutionException(startResult.getMessage());
            }
            Timer timer = new Timer();
            Responses responses = responseStorage.get(request.getTaskId());
            if (responses == null) {
                taskMonitoringService.incrementFailedTasksCount();
                throw new TaskExecutionException("no such task with given id " + request.getTaskId());
            }
            TaskExecutionResult result = responsesMapper.map(responses, timer, task);
            taskMonitoringService.incrementExecutedTasksCount();
            return result;
        } catch (IOException | ReducingException | TimeoutException | RuntimeException e) {
            e.printStackTrace();
            taskMonitoringService.incrementFailedTasksCount();
            throw new TaskExecutionException(e);
        } finally {
            responseStorage.remove(request.getTaskId());
        }
    }

    @Override
    public TaskExecutionResult executeTask(NodeRequest request) throws TaskExecutionException {
        return executeTask(request, 0);
    }

    void broadcastTask(NodeRequest request, SubTask task, int maxNodes) throws DistributionException {
        broadcastTask(request, task, maxNodes, 0);
    }

    private void broadcastTask(NodeRequest request, SubTask task, int maxNodes, int attempt)
            throws DistributionException {
        if (maxNodes < 0)
            throw new IllegalArgumentException("maxNodes must be at least 0");
        UUID taskId = request.getTaskId();
        int clusterSize = getClusterSize(request, task, maxNodes);
        if (clusterSize <= 0) {
            nodeLogger.debug("rebroadcast attempt " + attempt);
            if (attempt == MAX_ATTEMPTS) {
                throw new DistributionException("cannot broadcost more. system reached its max retry attempt.");
            }
            waitForGoodTimes();
            broadcastTask(request, task, maxNodes, attempt + 1);
            return;
        }
        nodeLogger.debug("involved nodes " + clusterSize);
        responseStorage.create(taskId, new Responses(clusterSize, task));
        List<NodeRequest> requests = task.getDistributor().distribute(request, clusterSize);
        nodeLogger.debug("requests to distribute " + requests);
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
        if (task.isSingleNodeTask(request))
            return 1;
        int actualClusterSize = slavesStorage.getClusterSize(request.getTaskName());
        return maxNodes == 0 ? actualClusterSize : Math.min(maxNodes, actualClusterSize);
    }

    TaskStartResult startTask(NodeRequest request, SubTask task, int maxNodes) {
        UUID taskId = request.getTaskId();
        DataValidators dataValidators = task.getDataValidators();
        if (!dataValidators.isValidRequest(request)) {
            return TaskStartResult.failed(taskId, "wrong arguments");
        } else if (task.isRequestDataTooBig(request)) {
            return TaskStartResult.failed(taskId, "too big arguments");
        }
        nodeLogger.debug("started task id is " + taskId);
        try {
            broadcastTask(request, task, maxNodes);
            return TaskStartResult.ok(taskId);
        } catch (DistributionException e) {
            nodeLogger.error(e);
            distributionService.broadcastStopRequests(slavesStorage.getListByTaskId(taskId), taskId);
            return TaskStartResult.systemError(taskId, e.getMessage());
        }
    }
}
