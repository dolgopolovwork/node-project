package ru.babobka.nodeslaveserver.task;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.SubTask;

public class TasksStorageTest {

    private SubTask mockTask = mock(SubTask.class);

    private TasksStorage storage;

    @Before
    public void setUp() {
	storage = new TasksStorage();
    }

    @After
    public void tearDown() {
	storage.clear();
    }

    @Test
    public void testStopTaskNormal() {
	UUID taskId = UUID.randomUUID();
	long timeStamp = System.currentTimeMillis() + 10;
	NodeRequest requestOne = NodeRequest.regular(taskId, "test", null);
	storage.put(requestOne, mockTask);
	storage.stopTask(taskId, timeStamp);
	assertTrue(storage.wasStopped(taskId, requestOne.getTimeStamp()));
    }

    @Test
    public void testStopTaskReorder() {
	UUID taskId = UUID.randomUUID();
	long timeStamp = System.currentTimeMillis() - 10;
	NodeRequest requestOne = NodeRequest.regular(taskId, "test", null);
	storage.put(requestOne, mockTask);
	storage.stopTask(taskId, timeStamp);
	assertFalse(storage.wasStopped(taskId, requestOne.getTimeStamp()));
    }

    @Test
    public void testStopTwoTaskNormal() {
	UUID taskId = UUID.randomUUID();
	long timeStamp = System.currentTimeMillis() + 10;
	NodeRequest requestOne = NodeRequest.regular(taskId, "test", null);
	NodeRequest requestTwo = NodeRequest.regular(taskId, "test", null);
	storage.put(requestOne, mockTask);
	storage.put(requestTwo, mockTask);
	storage.stopTask(taskId, timeStamp);
	assertTrue(storage.wasStopped(taskId, requestOne.getTimeStamp()));
	assertTrue(storage.wasStopped(taskId, requestTwo.getTimeStamp()));
    }

    @Test
    public void testStopTwoTaskReorder() {
	UUID taskId = UUID.randomUUID();
	long timeStamp = System.currentTimeMillis() - 10;
	NodeRequest requestOne = NodeRequest.regular(taskId, "test", null);
	NodeRequest requestTwo = NodeRequest.regular(taskId, "test", null);
	storage.stopTask(taskId, timeStamp);
	storage.put(requestOne, mockTask);
	storage.put(requestTwo, mockTask);
	assertFalse(storage.wasStopped(taskId, requestOne.getTimeStamp()));
	assertFalse(storage.wasStopped(taskId, requestTwo.getTimeStamp()));
    }

}
