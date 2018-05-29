package ru.babobka.nodetask.model;

import org.junit.Test;
import ru.babobka.nodeserials.data.Data;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 14.06.2017.
 */
public class ExecutionResultTest {

    @Test
    public void testNullMap() {
        ExecutionResult executionResult = new ExecutionResult(false, null);
        assertTrue(executionResult.getData().isEmpty());
    }

    @Test
    public void testStopped() {
        ExecutionResult executionResult = ExecutionResult.stopped();
        assertTrue(executionResult.isStopped());
        assertTrue(executionResult.getData().isEmpty());
    }

    @Test
    public void testMap() {
        Data data = new Data();
        data.put("abc", 123);
        ExecutionResult executionResult = new ExecutionResult(false, data);
        assertNotNull(executionResult.getData().get("abc"));
    }

}
