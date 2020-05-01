package ru.babobka.nodemasterserver.slave.pipeline.step;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.slave.pipeline.PipeContext;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static ru.babobka.nodemasterserver.key.MasterServerKey.MASTER_SERVER_TASK_POOL;

/**
 * Created by 123 on 08.06.2018.
 */
public class CommonTasksStepTest {


    private TaskPool taskPool;
    private PipeContext pipeContext;
    private CommonTasksStep commonTasksStep;
    private NodeConnection connection;

    @Before
    public void setUp() {
        connection = mock(NodeConnection.class);
        pipeContext = new PipeContext(connection);

        taskPool = mock(TaskPool.class);
        Container.getInstance().put(container -> {

            container.put(MASTER_SERVER_TASK_POOL, taskPool);
        });
        commonTasksStep = new CommonTasksStep();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testExecuteDoesNtContain() throws IOException {
        Set<String> availableTasks = new HashSet<>();
        when(connection.receive()).thenReturn(availableTasks);
        when(taskPool.containsAnyOfTask(availableTasks)).thenReturn(false);
        assertFalse(commonTasksStep.execute(pipeContext));
        verify(connection).send(false);
    }

    @Test
    public void testExecuteContains() throws IOException {
        Set<String> availableTasks = new HashSet<>();
        when(connection.receive()).thenReturn(availableTasks);
        when(taskPool.containsAnyOfTask(availableTasks)).thenReturn(true);
        assertTrue(commonTasksStep.execute(pipeContext));
        verify(connection).send(true);
    }

    @Test
    public void testExecuteException() throws IOException {
        when(connection.receive()).thenThrow(new IOException());
        assertFalse(commonTasksStep.execute(pipeContext));
    }
}