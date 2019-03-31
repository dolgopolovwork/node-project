package ru.babobka.nodeslaveserver.callback;

import org.junit.Test;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.network.NodeConnection;

import static org.mockito.Mockito.*;

public class NodeResponseCallbackTest {

    @Test
    public void testNonStoppedCallback() {
        NodeConnection connection = mock(NodeConnection.class);
        NodeResponseCallback callback = new NodeResponseCallback(connection);
        NodeResponse response = mock(NodeResponse.class);
        when(response.getStatus()).thenReturn(ResponseStatus.NORMAL);
        callback.callback(response);
        verify(connection).sendThrowRuntime(response);
    }

    @Test
    public void testStoppedCallback() {
        NodeConnection connection = mock(NodeConnection.class);
        NodeResponseCallback callback = new NodeResponseCallback(connection);
        NodeResponse response = mock(NodeResponse.class);
        when(response.getStatus()).thenReturn(ResponseStatus.STOPPED);
        callback.callback(response);
        verify(connection, never()).sendThrowRuntime(response);
    }
}
