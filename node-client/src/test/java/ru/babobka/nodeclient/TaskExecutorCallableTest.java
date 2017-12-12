package ru.babobka.nodeclient;

import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 16.12.2017.
 */
public class TaskExecutorCallableTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullRequest() {
        new TaskExecutorCallable(null, mock(NodeConnection.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullConnection() {
        new TaskExecutorCallable(mock(NodeRequest.class), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClosedConnection() {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(true);
        new TaskExecutorCallable(mock(NodeRequest.class), connection);
    }

    @Test
    public void testCallExceptionOnReceive() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(request, connection));
        doThrow(new IOException()).when(taskExecutorCallable).receiveResponse();
        assertEquals(taskExecutorCallable.call().getStatus(), ResponseStatus.FAILED);
        verify(connection).send(request);
        verify(connection).close();
    }

    @Test
    public void testCall() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        NodeResponse response = mock(NodeResponse.class);
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(request, connection));
        doReturn(response).when(taskExecutorCallable).receiveResponse();
        assertEquals(taskExecutorCallable.call(), response);
        verify(connection).send(request);
        verify(connection).close();
    }
}
