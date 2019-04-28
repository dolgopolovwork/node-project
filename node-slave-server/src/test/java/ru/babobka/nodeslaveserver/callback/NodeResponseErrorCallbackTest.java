package ru.babobka.nodeslaveserver.callback;

import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetask.exception.TaskExecutionException;
import ru.babobka.nodeutils.network.NodeConnection;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class NodeResponseErrorCallbackTest {

    @Test
    public void testCallbackIOException() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        NodeResponseErrorCallback callback = new NodeResponseErrorCallback(request, connection);
        callback.callback(new IOException());
        verify(connection, never()).send(any());
    }

    @Test
    public void testCallbackTaskException() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        NodeResponseErrorCallback callback = spy(new NodeResponseErrorCallback(request, connection));
        doNothing().when(callback).sendTaskError(any());
        callback.callback(new TaskExecutionException(ResponseStatus.SYSTEM_ERROR));
        verify(callback).sendTaskError(any());
    }


    @Test
    public void testCallbackRuntimeException() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        NodeResponseErrorCallback callback = spy(new NodeResponseErrorCallback(request, connection));
        doNothing().when(callback).sendTaskError(any());
        callback.callback(new RuntimeException());
        verify(callback).sendSystemError();
    }

    @Test(expected = NotImplementedException.class)
    public void testCreateErrorResponseNotError() {
        NodeRequest request = NodeRequest.heartBeat();
        NodeResponseErrorCallback.createErrorResponse(request, new TaskExecutionException(ResponseStatus.NORMAL));
    }

    @Test
    public void testCreateErrorResponseNoNodes() {
        NodeRequest request = NodeRequest.heartBeat();
        NodeResponse response = NodeResponseErrorCallback.createErrorResponse(request, new TaskExecutionException(ResponseStatus.NO_NODES));
        assertEquals(ResponseStatus.NO_NODES, response.getStatus());
        assertEquals(request.getTaskId(), response.getTaskId());
    }

    @Test
    public void testCreateErrorResponseSystemError() {
        NodeRequest request = NodeRequest.heartBeat();
        NodeResponse response = NodeResponseErrorCallback.createErrorResponse(request, new TaskExecutionException(ResponseStatus.SYSTEM_ERROR));
        assertEquals(ResponseStatus.SYSTEM_ERROR, response.getStatus());
        assertEquals(request.getTaskId(), response.getTaskId());
    }

    @Test
    public void testCreateErrorResponseValidationError() {
        NodeRequest request = NodeRequest.heartBeat();
        NodeResponse response = NodeResponseErrorCallback.createErrorResponse(request, new TaskExecutionException(ResponseStatus.VALIDATION_ERROR));
        assertEquals(ResponseStatus.VALIDATION_ERROR, response.getStatus());
        assertEquals(request.getTaskId(), response.getTaskId());
    }
}
