package ru.babobka.nodeslaveserver.callback;

import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeslaveserver.task.RaceStyleTaskStorage;
import ru.babobka.nodetask.exception.TaskExecutionException;
import ru.babobka.nodetask.service.TaskExecutionResult;
import ru.babobka.nodetask.service.TaskService;
import ru.babobka.nodeutils.func.Callback;
import ru.babobka.nodeutils.network.NodeConnection;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class NodeRequestCallbackTest {

    @Test
    public void testErrorResponse() {
        RaceStyleTaskStorage raceStyleTaskStorage = new RaceStyleTaskStorage();
        TaskService taskService = new TestableTaskService() {
            @Override
            public void executeTask(
                    NodeRequest request,
                    Callback<TaskExecutionResult> onTaskExecutedCallback,
                    Callback<TaskExecutionException> onError) {
                onError.callback(new TaskExecutionException(ResponseStatus.SYSTEM_ERROR));
            }
        };
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        NodeRequestCallback callback =
                spy(new NodeRequestCallback(raceStyleTaskStorage, taskService, connection));
        callback.callback(request);
        verify(callback).callOnErrorCallback(any(TaskExecutionException.class), eq(request));
    }

    @Test
    public void testRepeatedRaceRequest() {
        RaceStyleTaskStorage raceStyleTaskStorage = mock(RaceStyleTaskStorage.class);
        TaskExecutionResult result = mock(TaskExecutionResult.class);
        TaskService taskService = new TestableTaskService() {
            @Override
            public void executeTask(NodeRequest request, Callback<TaskExecutionResult> onTaskExecutedCallback, Callback<TaskExecutionException> onError) {
                onTaskExecutedCallback.callback(result);
            }
        };
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        when(raceStyleTaskStorage.isRepeated(request)).thenReturn(true);
        NodeRequestCallback callback =
                spy(new NodeRequestCallback(raceStyleTaskStorage, taskService, connection));
        callback.callback(request);
        verify(callback, never()).callOnErrorCallback(any(), any());
        verify(callback, never()).callOnResponseCallback(any(), any());
        verify(raceStyleTaskStorage).unregister(request);
    }

    @Test
    public void testNormalResponse() {
        RaceStyleTaskStorage raceStyleTaskStorage = mock(RaceStyleTaskStorage.class);
        TaskExecutionResult result = mock(TaskExecutionResult.class);
        TaskService taskService = new TestableTaskService() {
            @Override
            public void executeTask(NodeRequest request, Callback<TaskExecutionResult> onTaskExecutedCallback, Callback<TaskExecutionException> onError) {
                onTaskExecutedCallback.callback(result);
            }
        };
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        when(raceStyleTaskStorage.isRepeated(request)).thenReturn(false);
        NodeRequestCallback callback =
                spy(new NodeRequestCallback(raceStyleTaskStorage, taskService, connection));
        callback.callback(request);
        verify(callback, never()).callOnErrorCallback(any(), any());
        verify(callback).callOnResponseCallback(result, request);
        verify(raceStyleTaskStorage).unregister(request);
    }


    private abstract class TestableTaskService implements TaskService {

        @Override
        public void executeTask(NodeRequest request, int maxNodes, Callback<TaskExecutionResult> onTaskExecutedCallback, Callback<TaskExecutionException> onError) {

        }

        @Override
        public void executeTask(NodeRequest request, Callback<TaskExecutionResult> onTaskExecutedCallback, Callback<TaskExecutionException> onError) {

        }

        @Override
        public void cancelTask(UUID taskId, Callback<Boolean> onTaskCanceledCallback, Callback<TaskExecutionException> onError) {

        }
    }
}
