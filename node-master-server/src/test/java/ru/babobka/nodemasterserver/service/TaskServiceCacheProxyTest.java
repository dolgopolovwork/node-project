package ru.babobka.nodemasterserver.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodemasterserver.listener.CacheRequestListener;
import ru.babobka.nodemasterserver.monitoring.TaskMonitoringService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 18.02.2018.
 */
public class TaskServiceCacheProxyTest {

    private TaskPool taskPool;
    private TaskServiceImpl taskService;
    private CacheRequestListener cacheRequestListener;
    private TaskServiceCacheProxy taskServiceCacheProxy;
    private TaskMonitoringService taskMonitoringService;

    @Before
    public void setUp() {
        taskMonitoringService = mock(TaskMonitoringService.class);
        taskPool = mock(TaskPool.class);
        taskService = mock(TaskServiceImpl.class);
        cacheRequestListener = mock(CacheRequestListener.class);
        Container.getInstance().put(container -> {
            container.put(MasterServerKey.MASTER_SERVER_TASK_POOL, taskPool);
            container.put(cacheRequestListener);
            container.put(taskMonitoringService);
        });

        taskServiceCacheProxy = spy(new TaskServiceCacheProxy(taskService));
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testCanNotBeCached() throws IOException, TaskExecutionException {
        SubTask task = mock(SubTask.class);
        String taskName = "abc";
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskName()).thenReturn(taskName);
        when(taskPool.get(taskName)).thenReturn(task);
        when(task.isSingleNodeTask(request)).thenReturn(true);
        assertFalse(taskServiceCacheProxy.canBeCached(request));
        verify(taskMonitoringService, never()).incrementCacheHitCount();
    }

    @Test
    public void testCanBeCached() throws IOException, TaskExecutionException {
        SubTask task = mock(SubTask.class);
        when(task.enableCache()).thenReturn(true);
        String taskName = "abc";
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskName()).thenReturn(taskName);
        when(taskPool.get(taskName)).thenReturn(task);
        when(task.isSingleNodeTask(request)).thenReturn(false);
        assertTrue(taskServiceCacheProxy.canBeCached(request));
    }

    @Test
    public void testExecuteTaskCanNotBeCached() throws TaskExecutionException {
        NodeRequest request = mock(NodeRequest.class);
        doReturn(false).when(taskServiceCacheProxy).canBeCached(any(NodeRequest.class));
        TaskExecutionResult taskExecutionResult = mock(TaskExecutionResult.class);
        when(taskService.executeTask(eq(request), anyInt())).thenReturn(taskExecutionResult);
        assertEquals(taskExecutionResult, taskServiceCacheProxy.executeTask(request, 1));
        verify(taskService).executeTask(eq(request), anyInt());
        verify(cacheRequestListener, never()).onRequest(any(NodeRequest.class));
        verify(cacheRequestListener, never()).afterRequest(any(NodeRequest.class), any(TaskExecutionResult.class));
        verify(taskMonitoringService, never()).incrementCacheHitCount();
    }

    @Test
    public void testExecuteTaskCanBeCachedCacheHit() throws TaskExecutionException {
        NodeRequest request = mock(NodeRequest.class);
        doReturn(true).when(taskServiceCacheProxy).canBeCached(any(NodeRequest.class));
        TaskExecutionResult taskExecutionResult = mock(TaskExecutionResult.class);
        when(cacheRequestListener.onRequest(eq(request))).thenReturn(taskExecutionResult);
        assertEquals(taskExecutionResult, taskServiceCacheProxy.executeTask(request, 1));
        verify(taskService, never()).executeTask(eq(request), anyInt());
        verify(cacheRequestListener).onRequest(any(NodeRequest.class));
        verify(cacheRequestListener, never()).afterRequest(any(NodeRequest.class), any(TaskExecutionResult.class));
        verify(taskMonitoringService).incrementCacheHitCount();
    }

    @Test
    public void testExecuteTaskCanBeCachedCacheMiss() throws TaskExecutionException {
        NodeRequest request = mock(NodeRequest.class);
        doReturn(true).when(taskServiceCacheProxy).canBeCached(any(NodeRequest.class));
        TaskExecutionResult taskExecutionResult = mock(TaskExecutionResult.class);
        when(cacheRequestListener.onRequest(eq(request))).thenReturn(null);
        when(taskService.executeTask(eq(request), anyInt())).thenReturn(taskExecutionResult);
        assertEquals(taskExecutionResult, taskServiceCacheProxy.executeTask(request, 1));
        verify(taskService).executeTask(eq(request), anyInt());
        verify(cacheRequestListener).onRequest(any(NodeRequest.class));
        verify(cacheRequestListener).afterRequest(request, taskExecutionResult);
        verify(taskMonitoringService, never()).incrementCacheHitCount();
    }
}
