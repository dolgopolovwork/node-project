package ru.babobka.nodeclient;

import org.junit.Test;
import ru.babobka.nodeclient.listener.ListenerResult;
import ru.babobka.nodeclient.listener.OnResponseListener;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.func.done.DoneFunc;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 16.12.2017.
 */
public class TaskExecutorCallableTest {

    @Test(expected = NullPointerException.class)
    public void testNullRequest() {
        new TaskExecutorCallable(
                null,
                mock(NodeConnection.class),
                null,
                null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullConnection() {
        new TaskExecutorCallable(
                Arrays.asList(mock(NodeRequest.class), mock(NodeRequest.class)),
                null,
                null,
                null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClosedConnection() {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(true);
        new TaskExecutorCallable(
                Arrays.asList(mock(NodeRequest.class), mock(NodeRequest.class)),
                connection, null,
                new DoneFunc() {
                    @Override
                    public boolean isDone() {
                        return false;
                    }

                    @Override
                    public void setDone() {

                    }
                });
    }

    @Test(expected = NullPointerException.class)
    public void testNullDoneFunc() {
        NodeConnection connection = mock(NodeConnection.class);
        new TaskExecutorCallable(
                Arrays.asList(mock(NodeRequest.class), mock(NodeRequest.class)),
                connection, null,
                null);
    }

    @Test
    public void testCallExceptionOnReceive() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        List<NodeRequest> requests = Arrays.asList(request, request, request);
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(
                requests,
                connection,
                null,
                mock(DoneFunc.class)));
        doThrow(new IOException()).when(taskExecutorCallable).receiveResponse();
        assertTrue(taskExecutorCallable.call().isEmpty());
        verify(connection).send(requests);
        verify(connection).close();
    }

    @Test
    public void testCall() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        List<NodeRequest> requestList = Arrays.asList(request, request);
        NodeResponse response = mock(NodeResponse.class);
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(
                requestList,
                connection,
                null,
                mock(DoneFunc.class)));
        doReturn(response).when(taskExecutorCallable).receiveResponse();
        List<NodeResponse> responses = taskExecutorCallable.call();
        assertEquals(responses.size(), requestList.size());
        for (NodeResponse resultResponse : responses) {
            assertEquals(resultResponse, response);
        }
        verify(connection).send(requestList);
        verify(connection).close();
    }

    @Test
    public void testCallNullResponse() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        List<NodeRequest> requestList = Arrays.asList(request, request);
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(
                requestList,
                connection,
                null,
                mock(DoneFunc.class)));
        doReturn(null).when(taskExecutorCallable).receiveResponse();
        List<NodeResponse> responses = taskExecutorCallable.call();
        assertTrue(responses.isEmpty());
        verify(connection).close();
    }

    @Test
    public void testCallStopOnResponse() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        List<NodeRequest> requestList = Arrays.asList(request, request);
        NodeResponse response = mock(NodeResponse.class);
        OnResponseListener onResponseListener = mock(OnResponseListener.class);
        when(onResponseListener.onResponse(response)).thenReturn(ListenerResult.STOP);
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(
                requestList,
                connection,
                onResponseListener,
                mock(DoneFunc.class)));
        doReturn(response).when(taskExecutorCallable).receiveResponse();
        List<NodeResponse> responses = taskExecutorCallable.call();
        assertEquals(responses.size(), 1);
        verify(connection).send(requestList);
        verify(connection).close();
    }

    @Test
    public void testCallProceedOnResponse() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        List<NodeRequest> requestList = Arrays.asList(request, request);
        NodeResponse response = mock(NodeResponse.class);
        OnResponseListener onResponseListener = mock(OnResponseListener.class);
        when(onResponseListener.onResponse(response)).thenReturn(ListenerResult.PROCEED);
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(
                requestList,
                connection,
                onResponseListener,
                mock(DoneFunc.class)));
        doReturn(response).when(taskExecutorCallable).receiveResponse();
        List<NodeResponse> responses = taskExecutorCallable.call();
        assertEquals(responses.size(), requestList.size());
        for (NodeResponse resultResponse : responses) {
            assertEquals(resultResponse, response);
        }
        verify(connection).send(requestList);
        verify(connection).close();
    }

    @Test
    public void testCallDone() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        List<NodeRequest> requestList = Arrays.asList(request, request);
        DoneFunc doneFunc = mock(DoneFunc.class);
        when(doneFunc.isDone()).thenReturn(true);
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(
                requestList,
                connection,
                null,
                doneFunc));
        doReturn(null).when(taskExecutorCallable).receiveResponse();
        List<NodeResponse> responses = taskExecutorCallable.call();
        assertTrue(responses.isEmpty());
        verify(connection).close();
    }

    @Test
    public void testSendHeartBeat() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        List<NodeRequest> requestList = Arrays.asList(request, request);
        DoneFunc doneFunc = mock(DoneFunc.class);
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(
                requestList,
                connection,
                null,
                doneFunc));
        taskExecutorCallable.sendHeartBeat();
        verify(connection).setReadTimeOut(anyInt());
        verify(connection).send(any(NodeResponse.class));
    }

    @Test
    public void testReceiveResponseDone() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        List<NodeRequest> requestList = Arrays.asList(request, request);
        DoneFunc doneFunc = mock(DoneFunc.class);
        when(doneFunc.isDone()).thenReturn(true);
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(
                requestList,
                connection,
                null,
                doneFunc));
        assertNull(taskExecutorCallable.receiveResponse());
    }

    @Test
    public void testReceiveResponseHeartBeat() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        List<NodeRequest> requestList = Arrays.asList(request, request);
        DoneFunc doneFunc = mock(DoneFunc.class);
        when(doneFunc.isDone()).thenReturn(false, true);
        when(connection.receive()).thenReturn(NodeRequest.heartBeat());
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(
                requestList,
                connection,
                null,
                doneFunc));
        assertNull(taskExecutorCallable.receiveResponse());
        verify(taskExecutorCallable).sendHeartBeat();
    }

    @Test
    public void testReceiveResponse() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        NodeResponse response = mock(NodeResponse.class);
        List<NodeRequest> requestList = Arrays.asList(request, request);
        DoneFunc doneFunc = mock(DoneFunc.class);
        when(doneFunc.isDone()).thenReturn(false, true);
        when(connection.receive()).thenReturn(response);
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(
                requestList,
                connection,
                null,
                doneFunc));
        assertEquals(response, taskExecutorCallable.receiveResponse());
    }

    @Test
    public void testReceiveResponseTooLate() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        NodeResponse response = mock(NodeResponse.class);
        List<NodeRequest> requestList = Arrays.asList(request, request);
        DoneFunc doneFunc = mock(DoneFunc.class);
        when(doneFunc.isDone()).thenReturn(false, false, true);
        when(connection.receive()).thenReturn(NodeRequest.heartBeat(), NodeRequest.heartBeat(), response);
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(
                requestList,
                connection,
                null,
                doneFunc));
        assertNull(taskExecutorCallable.receiveResponse());
        verify(taskExecutorCallable, times(2)).sendHeartBeat();
    }

    @Test
    public void testReceiveResponseRightInTime() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        NodeResponse response = mock(NodeResponse.class);
        List<NodeRequest> requestList = Arrays.asList(request, request);
        DoneFunc doneFunc = mock(DoneFunc.class);
        when(doneFunc.isDone()).thenReturn(false, false, false, true);
        when(connection.receive()).thenReturn(NodeRequest.heartBeat(), NodeRequest.heartBeat(), response);
        TaskExecutorCallable taskExecutorCallable = spy(new TaskExecutorCallable(
                requestList,
                connection,
                null,
                doneFunc));
        assertEquals(response, taskExecutorCallable.receiveResponse());
        verify(taskExecutorCallable, times(2)).sendHeartBeat();
    }
}
