package ru.babobka.nodeslaveserver.task;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodetask.model.DataValidators;
import ru.babobka.nodetask.model.ExecutionResult;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodetask.model.TaskExecutor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 27.10.2017.
 */
public class TaskRunnerServiceTest {

    private final TaskRunnerService taskRunnerService = new TaskRunnerService();
    private TasksStorage tasksStorage;
    private SubTask subTask;
    private NodeRequest request;
    private DataValidators dataValidators;
    private TaskExecutor taskExecutor;

    @Before
    public void setUp() {
        tasksStorage = mock(TasksStorage.class);
        subTask = mock(SubTask.class);
        request = mock(NodeRequest.class);
        dataValidators = mock(DataValidators.class);
        taskExecutor = mock(TaskExecutor.class);
        when(subTask.getDataValidators()).thenReturn(dataValidators);
        when(subTask.getTaskExecutor()).thenReturn(taskExecutor);
    }

    @Test
    public void testRunTaskBadValidation() {
        when(dataValidators.isValidRequest(any(NodeRequest.class))).thenReturn(false);
        NodeResponse response = taskRunnerService.runTask(tasksStorage, request, subTask);
        assertEquals(response.getStatus(), ResponseStatus.FAILED);
        verify(tasksStorage).removeRequest(request);
    }

    @Test
    public void testRunTaskStopped() {
        when(dataValidators.isValidRequest(any(NodeRequest.class))).thenReturn(true);
        ExecutionResult result = mock(ExecutionResult.class);
        when(result.isStopped()).thenReturn(true);
        when(taskExecutor.execute(request)).thenReturn(result);
        NodeResponse response = taskRunnerService.runTask(tasksStorage, request, subTask);
        assertEquals(response.getStatus(), ResponseStatus.STOPPED);
        verify(tasksStorage).removeRequest(request);
    }

    @Test
    public void testRunTask() {
        when(dataValidators.isValidRequest(any(NodeRequest.class))).thenReturn(true);
        ExecutionResult result = mock(ExecutionResult.class);
        when(result.isStopped()).thenReturn(false);
        when(taskExecutor.execute(request)).thenReturn(result);
        NodeResponse response = taskRunnerService.runTask(tasksStorage, request, subTask);
        assertEquals(response.getStatus(), ResponseStatus.NORMAL);
        verify(tasksStorage).removeRequest(request);
    }
}
