package ru.babobka.nodeslaveserver.server.pipeline.step;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodesecurity.auth.AuthCredentials;
import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.babobka.nodeslaveserver.key.SlaveServerKey.SLAVE_SERVER_TASK_POOL;

/**
 * Created by 123 on 09.06.2018.
 */
public class CommonTasksStepTest {

    private TaskPool taskPool;
    private NodeConnection connection;
    private PipeContext pipeContext;
    private CommonTasksStep commonTasksStep;

    @Before
    public void setUp() {
        connection = mock(NodeConnection.class);

        pipeContext = new PipeContext(connection, mock(AuthCredentials.class));
        taskPool = mock(TaskPool.class);
        Container.getInstance().put(container -> {

            container.put(SLAVE_SERVER_TASK_POOL, taskPool);
        });
        commonTasksStep = new CommonTasksStep();
    }

    @Test
    public void testExecuteNoCommonTasks() throws IOException {
        Set<String> taskNames = new HashSet<>();
        when(taskPool.getTaskNames()).thenReturn(taskNames);
        when(connection.receive()).thenReturn(false);
        assertFalse(commonTasksStep.execute(pipeContext));
    }

    @Test
    public void testExecuteHasCommonTasks() throws IOException {
        Set<String> taskNames = new HashSet<>();
        when(taskPool.getTaskNames()).thenReturn(taskNames);
        when(connection.receive()).thenReturn(true);
        assertTrue(commonTasksStep.execute(pipeContext));
    }

    @Test
    public void testExecuteException() throws IOException {
        Set<String> taskNames = new HashSet<>();
        when(taskPool.getTaskNames()).thenReturn(taskNames);
        when(connection.receive()).thenThrow(new IOException());
        assertFalse(commonTasksStep.execute(pipeContext));
    }
}
