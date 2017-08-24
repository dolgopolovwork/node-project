package ru.babobka.nodemasterserver.applyer;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 18.09.2017.
 */
public class GroupTaskApplyerTest {

    private GroupTaskApplyer groupTaskApplyer;

    @Before
    public void setUp() {
        groupTaskApplyer = new GroupTaskApplyer();
    }

    @Test
    public void testApplyNotExistingTask() {
        String taskName = "task name";
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskName()).thenReturn(taskName);
        groupTaskApplyer.apply(request);
        assertTrue(groupTaskApplyer.getGroupedTasks().containsKey(taskName));
    }

    @Test
    public void testApplyExisting() {
        String taskName = "task name";
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskName()).thenReturn(taskName);
        groupTaskApplyer.apply(request);
        groupTaskApplyer.apply(request);
        assertTrue(groupTaskApplyer.getGroupedTasks().containsKey(taskName));
        assertEquals(groupTaskApplyer.getGroupedTasks().get(taskName).size(), 2);
    }

    @Test
    public void testApplyDifferent() {
        String taskName = "task name";
        NodeRequest request1 = mock(NodeRequest.class);
        when(request1.getTaskName()).thenReturn(taskName);
        NodeRequest request2 = mock(NodeRequest.class);
        when(request2.getTaskName()).thenReturn("another task name");
        groupTaskApplyer.apply(request1);
        groupTaskApplyer.apply(request2);
        assertEquals(groupTaskApplyer.getGroupedTasks().size(), 2);
        assertEquals(groupTaskApplyer.getGroupedTasks().get(taskName).size(), 1);
    }
}
