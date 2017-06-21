package ru.babobka.subtask.model;

import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 14.06.2017.
 */
public class ExecutionResultTest {

    @Test
    public void testNullMap() {
        ExecutionResult executionResult = new ExecutionResult(false, null);
        assertTrue(executionResult.getResultMap().isEmpty());
    }

    @Test
    public void testStopped() {
        ExecutionResult executionResult = ExecutionResult.stopped();
        assertTrue(executionResult.isStopped());
        assertTrue(executionResult.getResultMap().isEmpty());
    }

    @Test
    public void testMap() {
        Map<String, Serializable> map = new HashMap<>();
        map.put("abc", 123);
        ExecutionResult executionResult = new ExecutionResult(false, map);
        assertTrue(executionResult.getResultMap().containsKey("abc"));
    }

}
