package ru.babobka.nodemasterserver.model;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeserials.NodeResponse;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 14.09.2017.
 */
public class ResponseStorageTest {

    private ResponseStorage responseStorage;

    @Before
    public void setUp() {
        responseStorage = new ResponseStorage();
    }

    @Test
    public void testCreate() {
        UUID taskId = UUID.randomUUID();
        responseStorage.create(taskId, mock(Responses.class));
        assertTrue(responseStorage.exists(taskId));
    }

    @Test
    public void testDoesntExist() {
        UUID taskId = UUID.randomUUID();
        assertFalse(responseStorage.exists(taskId));
    }

    @Test
    public void testGet() {
        Responses responses = mock(Responses.class);
        UUID taskId = UUID.randomUUID();
        responseStorage.create(taskId, responses);
        assertEquals(responses, responseStorage.get(taskId));
    }

    @Test
    public void testGetUnexisting() {
        assertNull(responseStorage.get(UUID.randomUUID()));
    }

    @Test
    public void testAddBadResponseNotExistingTask() {
        assertFalse(responseStorage.addBadResponse(UUID.randomUUID()));
    }

    @Test
    public void testAddBadResponseIsFilled() {
        Responses responses = mock(Responses.class);
        when(responses.add(any(NodeResponse.class))).thenReturn(false);
        UUID taskId = UUID.randomUUID();
        responseStorage.create(taskId, responses);
        assertFalse(responseStorage.addBadResponse(taskId));
        verify(responses).add(any(NodeResponse.class));
    }

    @Test
    public void testAddBadResponse() {
        Responses responses = mock(Responses.class);
        when(responses.add(any(NodeResponse.class))).thenReturn(true);
        UUID taskId = UUID.randomUUID();
        responseStorage.create(taskId, responses);
        assertTrue(responseStorage.addBadResponse(taskId));
        verify(responses).add(any(NodeResponse.class));
    }

    @Test
    public void testAddStopResponseNotExistingTask() {
        assertFalse(responseStorage.addStopResponse(UUID.randomUUID()));
    }

    @Test
    public void testAddStopResponseIsFilled() {
        Responses responses = mock(Responses.class);
        when(responses.add(any(NodeResponse.class))).thenReturn(false);
        UUID taskId = UUID.randomUUID();
        responseStorage.create(taskId, responses);
        assertFalse(responseStorage.addStopResponse(taskId));
        verify(responses).add(any(NodeResponse.class));
    }

    @Test
    public void testAddStopResponse() {
        Responses responses = mock(Responses.class);
        when(responses.add(any(NodeResponse.class))).thenReturn(true);
        UUID taskId = UUID.randomUUID();
        responseStorage.create(taskId, responses);
        assertTrue(responseStorage.addStopResponse(taskId));
        verify(responses).add(any(NodeResponse.class));
    }

    @Test
    public void testSetStopAllResponsesNotExistingTask() {
        assertFalse(responseStorage.setStopAllResponses(UUID.randomUUID()));
    }

    @Test
    public void testSetStopAllResponsesIsFilled() {
        Responses responses = mock(Responses.class);
        when(responses.fill(any(NodeResponse.class))).thenReturn(false);
        UUID taskId = UUID.randomUUID();
        responseStorage.create(taskId, responses);
        assertFalse(responseStorage.setStopAllResponses(taskId));
        verify(responses).fill(any(NodeResponse.class));
    }

    @Test
    public void testSetStopAllResponses() {
        Responses responses = mock(Responses.class);
        when(responses.fill(any(NodeResponse.class))).thenReturn(true);
        UUID taskId = UUID.randomUUID();
        responseStorage.create(taskId, responses);
        assertTrue(responseStorage.setStopAllResponses(taskId));
        verify(responses).fill(any(NodeResponse.class));
    }

    @Test
    public void testRemove() {
        UUID taskId = UUID.randomUUID();
        responseStorage.create(taskId, mock(Responses.class));
        responseStorage.remove(taskId);
        assertFalse(responseStorage.exists(taskId));
    }

    @Test
    public void testGetTaskMetaUnexisting() {
        assertNull(responseStorage.get(UUID.randomUUID()));
    }
}
