package ru.babobka.nodeslaveserver.runnable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 02.09.2017.
 */
public class RequestHandlerRunnableTest {

    private SimpleLogger logger;
    private TaskRunnerService taskRunnerService;

    @Before
    public void setUp() {
        logger = mock(SimpleLogger.class);
        taskRunnerService = mock(TaskRunnerService.class);
        Container.getInstance().put(logger);
        Container.getInstance().put(taskRunnerService);
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testRunRegular() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeResponse response = NodeResponse.dummy(UUID.randomUUID());
        NodeRequest request = NodeRequest.heartBeatRequest();
        when(taskRunnerService.runTask(any(TasksStorage.class), any(NodeRequest.class), any(SubTask.class))).thenReturn(response);
        new RequestHandlerRunnable(connection, mock(TasksStorage.class), request, mock(SubTask.class)).run();
        verify(taskRunnerService).runTask(any(TasksStorage.class), any(NodeRequest.class), any(SubTask.class));
        verify(connection).send(response);
    }

    @Test
    public void testRunIOException() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeResponse response = NodeResponse.dummy(UUID.randomUUID());
        NodeRequest request = NodeRequest.heartBeatRequest();
        when(taskRunnerService.runTask(any(TasksStorage.class), any(NodeRequest.class), any(SubTask.class))).thenReturn(response);
        doThrow(new IOException()).when(connection).send(any(NodeResponse.class));
        new RequestHandlerRunnable(connection, mock(TasksStorage.class), request, mock(SubTask.class)).run();
        verify(logger).error(anyString(), any(Exception.class));
    }

    @Test
    public void testRunRuntimeException() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = NodeRequest.heartBeatRequest();
        when(taskRunnerService.runTask(any(TasksStorage.class), any(NodeRequest.class), any(SubTask.class))).thenThrow(new RuntimeException());
        new RequestHandlerRunnable(connection, mock(TasksStorage.class), request, mock(SubTask.class)).run();
        verify(logger).error(any(Exception.class));
        verify(connection).send(any(NodeResponse.class));
    }

}
