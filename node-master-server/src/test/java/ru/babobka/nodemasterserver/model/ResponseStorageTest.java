package ru.babobka.nodemasterserver.model;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResponseStorageTest {

    private ResponseStorage responseStorage;

    private ResponsesArray mockResponses = ResponsesArray.dummyResponsesArray();

    @Before
    public void setUp() {
	responseStorage = new ResponseStorage();
    }

    @Test
    public void testPut() {
	UUID taskId = UUID.randomUUID();
	responseStorage.create(taskId, mockResponses);
	assertTrue(responseStorage.exists(taskId));

    }

    @Test
    public void testPutAlot() {
	int puts = 10000;
	for (int i = 0; i < puts; i++) {
	    responseStorage.create(UUID.randomUUID(), mockResponses);
	}
	assertEquals(puts, responseStorage.size());

    }

    @Test
    public void testRunning() {
	int puts = 10000;
	for (int i = 0; i < puts; i++) {
	    responseStorage.create(UUID.randomUUID(), mockResponses);
	}
	assertEquals(responseStorage.getRunningTasksMetaMap().size(), puts);
    }

    @Test
    public void testRemove() {
	UUID taskId = UUID.randomUUID();
	responseStorage.create(taskId, mockResponses);
	responseStorage.remove(taskId);
	assertFalse(responseStorage.exists(taskId));
	assertEquals(0, responseStorage.size());
    }

}
