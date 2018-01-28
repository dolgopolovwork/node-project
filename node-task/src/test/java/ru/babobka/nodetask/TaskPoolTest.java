package ru.babobka.nodetask;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodetask.exception.CanNotInitTaskFactoryException;
import ru.babobka.nodetask.model.TaskFactory;
import ru.babobka.nodetask.util.TasksUtil;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 02.09.2017.
 */
public class TaskPoolTest {

    private StreamUtil streamUtil;

    private TasksUtil tasksUtil;

    @Before
    public void setUp() {
        streamUtil = mock(StreamUtil.class);
        tasksUtil = mock(TasksUtil.class);
        Container.getInstance().put(streamUtil);
        Container.getInstance().put(tasksUtil);
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testFillTasksMap() throws IOException {
        TaskFactory taskFactory = mock(TaskFactory.class);
        ApplicationContainer applicationContainer = mock(ApplicationContainer.class);
        when(taskFactory.getApplicationContainer()).thenReturn(applicationContainer);
        when(taskFactory.getTaskType()).thenReturn(Class.class);
        List<String> jars = Arrays.asList("abc", "xyz", "qwe");
        when(streamUtil.getJarFileListFromFolder(anyString())).thenReturn(jars);
        List<TaskFactory> taskFactories = Arrays.asList(taskFactory, taskFactory, taskFactory);
        when(tasksUtil.getFactories(anyString())).thenReturn(taskFactories);
        TaskPool taskPool = new TaskPool("/");
        assertFalse(taskPool.isEmpty());
        verify(applicationContainer, times(taskFactories.size() * jars.size())).contain(any(Container.class));
    }

    @Test(expected = CanNotInitTaskFactoryException.class)
    public void testFillTasksMapIOException() throws IOException {
        List<String> jars = Arrays.asList("abc", "xyz", "qwe");
        when(streamUtil.getJarFileListFromFolder(anyString())).thenReturn(jars);
        when(tasksUtil.getFactories(anyString())).thenThrow(new IOException());
        new TaskPool("/");
    }

    @Test(expected = CanNotInitTaskFactoryException.class)
    public void testFillTasksMapRuntimeException() throws IOException {
        List<String> jars = Arrays.asList("abc", "xyz", "qwe");
        when(streamUtil.getJarFileListFromFolder(anyString())).thenReturn(jars);
        when(tasksUtil.getFactories(anyString())).thenThrow(new RuntimeException());
        new TaskPool("/");
    }

    @Test(expected = CanNotInitTaskFactoryException.class)
    public void testFillTasksMapNoJars() throws IOException {
        when(streamUtil.getJarFileListFromFolder(anyString())).thenReturn(new LinkedList<>());
        new TaskPool("/");
    }

    @Test(expected = CanNotInitTaskFactoryException.class)
    public void testFillTasksMapNoTasks() throws IOException {
        List<String> jars = Arrays.asList("abc", "xyz", "qwe");
        when(streamUtil.getJarFileListFromFolder(anyString())).thenReturn(jars);
        when(tasksUtil.getFactories(anyString())).thenReturn(new LinkedList<>());
        new TaskPool("/");
    }
}
