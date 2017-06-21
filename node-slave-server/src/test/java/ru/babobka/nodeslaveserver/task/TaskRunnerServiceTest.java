package ru.babobka.nodeslaveserver.task;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.subtask.model.*;

import java.io.FileNotFoundException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 20.06.2017.
 */
public class TaskRunnerServiceTest {

    private TaskRunnerService taskRunnerService = new TaskRunnerService();

    @BeforeClass
    public static void setUp() throws FileNotFoundException {
        SlaveServer.initTestContainer();
    }

    @Test
    public void testRunTaskStopped() {
        SubTask subTask = mock(SubTask.class);
        ValidationResult validationResult = ValidationResult.ok();
        RequestValidator requestValidator = mock(RequestValidator.class);
        when(requestValidator.validateRequest(any(NodeRequest.class))).thenReturn(validationResult);
        when(subTask.getRequestValidator()).thenReturn(requestValidator);
        ExecutionResult executionResult = ExecutionResult.stopped();
        TaskExecutor taskExecutor = mock(TaskExecutor.class);
        when(taskExecutor.execute(any(Integer.class), any(NodeRequest.class))).thenReturn(executionResult);
        when(subTask.getTaskExecutor()).thenReturn(taskExecutor);
        NodeResponse response = taskRunnerService.runTask(mock(TasksStorage.class), NodeRequest.heartBeatRequest(), subTask);
        assertEquals(response.getStatus(), NodeResponse.Status.STOPPED);
    }

    @Test
    public void testRunTaskOk() {
        SubTask subTask = mock(SubTask.class);
        ValidationResult validationResult = ValidationResult.ok();
        RequestValidator requestValidator = mock(RequestValidator.class);
        when(requestValidator.validateRequest(any(NodeRequest.class))).thenReturn(validationResult);
        when(subTask.getRequestValidator()).thenReturn(requestValidator);
        ExecutionResult executionResult = new ExecutionResult(false, new HashMap<>());
        TaskExecutor taskExecutor = mock(TaskExecutor.class);
        when(taskExecutor.execute(any(Integer.class), any(NodeRequest.class))).thenReturn(executionResult);
        when(subTask.getTaskExecutor()).thenReturn(taskExecutor);
        NodeResponse response = taskRunnerService.runTask(mock(TasksStorage.class), NodeRequest.heartBeatRequest(), subTask);
        assertEquals(response.getStatus(), NodeResponse.Status.NORMAL);
    }

    @Test
    public void testRunTaskFailed() {
        SubTask subTask = mock(SubTask.class);
        ValidationResult validationResult = ValidationResult.fail("test");
        RequestValidator requestValidator = mock(RequestValidator.class);
        when(requestValidator.validateRequest(any(NodeRequest.class))).thenReturn(validationResult);
        when(subTask.getRequestValidator()).thenReturn(requestValidator);
        NodeResponse response = taskRunnerService.runTask(mock(TasksStorage.class), NodeRequest.heartBeatRequest(), subTask);
        assertEquals(response.getStatus(), NodeResponse.Status.FAILED);
    }

}