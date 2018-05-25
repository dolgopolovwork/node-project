package ru.babobka.nodemasterserver.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.listener.OnRaceStyleTaskIsReady;
import ru.babobka.nodemasterserver.listener.OnResponseListener;
import ru.babobka.nodemasterserver.listener.OnTaskIsReady;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetask.model.DataValidators;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 11.09.2017.
 */
public class ResponsesTest {
    private NodeLogger nodeLogger;
    private OnResponseListener taskIsReadyListener;
    private OnResponseListener raceStyleTaskIsReadyListener;
    private SubTask task;

    @Before
    public void setUp() {
        task = mock(SubTask.class);
        when(task.getName()).thenReturn("test");
        nodeLogger = mock(NodeLogger.class);
        raceStyleTaskIsReadyListener = mock(OnRaceStyleTaskIsReady.class);
        taskIsReadyListener = mock(OnTaskIsReady.class);
        Container.getInstance().put(nodeLogger);
        Container.getInstance().put(taskIsReadyListener);
        Container.getInstance().put(raceStyleTaskIsReadyListener);
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadMaxSize() {
        new Responses(0, task, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullTaskContext() {
        new Responses(1, null, null);
    }

    @Test
    public void testIsCompleteFalse() {
        assertFalse(new Responses(1, task, null).isComplete());
    }

    @Test
    public void testIsCompleteTrue() {
        Responses responses = new Responses(1, task, null);
        responses.add(NodeResponse.dummy(UUID.randomUUID()));
        assertTrue(responses.isComplete());
    }

    @Test
    public void testAdd() {
        NodeResponse response = NodeResponse.dummy(UUID.randomUUID());
        Responses responses = new Responses(1, task, null);
        assertTrue(responses.add(response));
        verify(taskIsReadyListener).onResponse(response);
    }

    @Test
    public void testAddRaceStyle() {
        when(task.isRaceStyle()).thenReturn(true);
        DataValidators dataValidators = mock(DataValidators.class);
        when(dataValidators.isValidResponse(any(NodeResponse.class))).thenReturn(true);
        when(task.getDataValidators()).thenReturn(dataValidators);
        NodeResponse response = NodeResponse.dummy(UUID.randomUUID());
        Responses responses = new Responses(2, task, null);
        assertTrue(responses.add(response));
        verify(raceStyleTaskIsReadyListener).onResponse(response);
    }

    @Test
    public void testAddIsComplete() {
        NodeResponse response = NodeResponse.dummy(UUID.randomUUID());
        Responses responses = new Responses(1, task, null);
        assertTrue(responses.add(response));
        assertFalse(responses.add(response));
    }

    @Test
    public void testFillComplete() {
        NodeResponse response = NodeResponse.dummy(UUID.randomUUID());
        Responses responses = new Responses(1, task, null);
        responses.add(response);
        assertFalse(responses.fill(response));
    }

    @Test
    public void testFillNotComplete() {
        NodeResponse response = NodeResponse.dummy(UUID.randomUUID());
        Responses responses = new Responses(2, task, null);
        responses.add(response);
        assertTrue(responses.fill(response));
        verify(taskIsReadyListener).onResponse(response);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResponseListBadTimeout() throws TimeoutException {
        new Responses(1, task, null).getResponseList(0);
    }

    @Test
    public void testGetResponseList() throws TimeoutException {
        NodeResponse response1 = NodeResponse.dummy(UUID.randomUUID());
        NodeResponse response2 = NodeResponse.dummy(UUID.randomUUID());
        List<NodeResponse> nodeResponses = Arrays.asList(response1, response2);
        Responses responses = new Responses(nodeResponses.size(), task, null);
        for (NodeResponse nodeResponse : nodeResponses) {
            responses.add(nodeResponse);
        }
        assertEquals(responses.getResponseList(1).size(), nodeResponses.size());
    }

    @Test
    public void testGetResponseListWait() throws TimeoutException {
        long waitMillis = 5000L;
        NodeResponse response1 = NodeResponse.dummy(UUID.randomUUID());
        NodeResponse response2 = NodeResponse.dummy(UUID.randomUUID());
        List<NodeResponse> nodeResponses = Arrays.asList(response1, response2);
        final Responses responses = new Responses(nodeResponses.size(), task, null);
        new Thread(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (NodeResponse nodeResponse : nodeResponses) {
                responses.add(nodeResponse);
            }
        }).start();
        assertEquals(responses.getResponseList(waitMillis).size(), nodeResponses.size());
    }

    @Test(expected = TimeoutException.class)
    public void testGetResponseListTimeout() throws TimeoutException {
        Responses responses = new Responses(1, task, null);
        responses.getResponseList(100L);
    }

    @Test
    public void testAlreadyHasResponseEmpty() {
        Responses responses = new Responses(1, task, null);
        assertFalse(responses.alreadyHasResponse(mock(NodeResponse.class)));
    }

    @Test
    public void testAlreadyHasResponseNoDuplicate() {
        Responses responses = new Responses(2, task, null);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        NodeResponse response1 = mock(NodeResponse.class);
        when(response1.getId()).thenReturn(id1);
        NodeResponse response2 = mock(NodeResponse.class);
        when(response2.getId()).thenReturn(id2);
        responses.add(response1);
        assertFalse(responses.alreadyHasResponse(response2));
    }

    @Test
    public void testAlreadyHasResponse() {
        Responses responses = new Responses(2, task, null);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        NodeResponse response1 = mock(NodeResponse.class);
        when(response1.getId()).thenReturn(id1);
        NodeResponse response2 = mock(NodeResponse.class);
        when(response2.getId()).thenReturn(id2);
        responses.add(response1);
        responses.add(response2);
        assertTrue(responses.alreadyHasResponse(response2));
    }

    @Test
    public void testIsStoppedEmpty() {
        Responses responses = new Responses(2, task, null);
        assertFalse(responses.isStopped());
    }

    @Test
    public void testIsStoppedNoStopped() {
        Responses responses = new Responses(2, task, null);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        NodeResponse response1 = mock(NodeResponse.class);
        when(response1.getId()).thenReturn(id1);
        NodeResponse response2 = mock(NodeResponse.class);
        when(response2.getId()).thenReturn(id2);
        responses.add(response1);
        responses.add(response2);
        assertFalse(responses.isStopped());
    }

    @Test
    public void testIsStoppedNotAll() {
        Responses responses = new Responses(2, task, null);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        NodeResponse response1 = mock(NodeResponse.class);
        when(response1.getStatus()).thenReturn(ResponseStatus.STOPPED);
        when(response1.getId()).thenReturn(id1);
        NodeResponse response2 = mock(NodeResponse.class);
        when(response2.getId()).thenReturn(id2);
        responses.add(response1);
        responses.add(response2);
        assertFalse(responses.isStopped());
    }

    @Test
    public void testIsStopped() {
        Responses responses = new Responses(2, task, null);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        NodeResponse response1 = mock(NodeResponse.class);
        when(response1.getStatus()).thenReturn(ResponseStatus.STOPPED);
        when(response1.getId()).thenReturn(id1);
        NodeResponse response2 = mock(NodeResponse.class);
        when(response2.getId()).thenReturn(id2);
        when(response2.getStatus()).thenReturn(ResponseStatus.STOPPED);
        responses.add(response1);
        responses.add(response2);
        assertTrue(responses.isStopped());
    }
}