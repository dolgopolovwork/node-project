package ru.babobka.nodetask;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodetask.model.TaskExecutor;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TasksStorageTest {

    private static SubTask mockTask;

    private TasksStorage storage;


    @BeforeClass
    public static void taskInit() {
        mockTask = mock(SubTask.class);
        when(mockTask.getTaskExecutor()).thenReturn(mock(TaskExecutor.class));
    }

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
        NodeRequest requestOne = mock(NodeRequest.class);
        when(requestOne.getTaskId()).thenReturn(taskId);
        when(requestOne.getTimeStamp()).thenReturn(timeStamp - 10);
        storage.put(requestOne, mockTask);
        storage.stopTask(taskId, timeStamp);
        assertTrue(storage.wasStopped(requestOne));
    }

    @Test
    public void testStopTaskReorder() {
        UUID taskId = UUID.randomUUID();
        long timeStamp = System.currentTimeMillis() - 10;
        NodeRequest requestOne = mock(NodeRequest.class);
        when(requestOne.getTaskId()).thenReturn(taskId);
        when(requestOne.getTimeStamp()).thenReturn(timeStamp + 10);
        storage.put(requestOne, mockTask);
        storage.stopTask(taskId, timeStamp);
        assertFalse(storage.wasStopped(requestOne));
    }

    @Test
    public void testStopTwoTaskNormal() {
        UUID taskId = UUID.randomUUID();
        long timeStamp = System.currentTimeMillis() + 10;
        NodeRequest requestOne = mock(NodeRequest.class);
        when(requestOne.getTaskId()).thenReturn(taskId);
        when(requestOne.getTimeStamp()).thenReturn(timeStamp - 10);
        NodeRequest requestTwo = mock(NodeRequest.class);
        when(requestTwo.getTaskId()).thenReturn(taskId);
        when(requestTwo.getTimeStamp()).thenReturn(timeStamp - 10);
        storage.put(requestOne, mockTask);
        storage.put(requestTwo, mockTask);
        storage.stopTask(taskId, timeStamp);
        assertTrue(storage.wasStopped(requestOne));
        assertTrue(storage.wasStopped(requestTwo));
    }

    @Test
    public void testStopTwoTaskReorder() {
        UUID taskId = UUID.randomUUID();
        long timeStamp = System.currentTimeMillis() - 10;
        NodeRequest requestOne = mock(NodeRequest.class);
        when(requestOne.getTaskId()).thenReturn(taskId);
        when(requestOne.getTimeStamp()).thenReturn(timeStamp);
        NodeRequest requestTwo = mock(NodeRequest.class);
        when(requestTwo.getTaskId()).thenReturn(taskId);
        when(requestTwo.getTimeStamp()).thenReturn(timeStamp);
        storage.stopTask(taskId, timeStamp);
        storage.put(requestOne, mockTask);
        storage.put(requestTwo, mockTask);
        assertFalse(storage.wasStopped(requestOne));
        assertFalse(storage.wasStopped(requestTwo));
    }

}
