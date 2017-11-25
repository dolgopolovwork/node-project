package ru.babobka.nodemasterserver.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.listener.OnRaceStyleTaskIsReady;
import ru.babobka.nodemasterserver.listener.OnResponseListener;
import ru.babobka.nodemasterserver.listener.OnTaskIsReady;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.model.DataValidators;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

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
    private SimpleLogger logger;
    private OnResponseListener taskIsReadyListener;
    private OnResponseListener raceStyleTaskIsReadyListener;
    private SubTask task;

    @Before
    public void setUp() {
        task = mock(SubTask.class);
        when(task.getName()).thenReturn("test");
        logger = mock(SimpleLogger.class);
        raceStyleTaskIsReadyListener = mock(OnRaceStyleTaskIsReady.class);
        taskIsReadyListener = mock(OnTaskIsReady.class);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(logger);
                container.put(taskIsReadyListener);
                container.put(raceStyleTaskIsReadyListener);
            }
        }.contain(Container.getInstance());
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (NodeResponse nodeResponse : nodeResponses) {
                    responses.add(nodeResponse);
                }
            }
        }).start();
        assertEquals(responses.getResponseList(waitMillis).size(), nodeResponses.size());
    }

    @Test(expected = TimeoutException.class)
    public void testGetResponseListTimeout() throws TimeoutException {
        Responses responses = new Responses(1, task, null);
        responses.getResponseList(100L);
    }
}