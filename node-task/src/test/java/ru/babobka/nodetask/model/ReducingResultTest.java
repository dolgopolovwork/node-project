package ru.babobka.nodetask.model;

import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by 123 on 14.06.2017.
 */
public class ReducingResultTest {

    @Test(expected = IllegalArgumentException.class)
    public void testAddNull() {
        new ReducingResult().add(null);
    }

    @Test
    public void testAddMap() {
        ReducingResult reducingResult = new ReducingResult();
        Map<String, Serializable> map = new HashMap<>();
        map.put("abc", 123);
        reducingResult.add(map);
        assertFalse(reducingResult.map().isEmpty());
        assertTrue(reducingResult.map().containsKey("abc"));
    }

    @Test
    public void testAddKeyValue() {
        ReducingResult reducingResult = new ReducingResult();
        reducingResult.add("abc", 123);
        assertTrue(reducingResult.map().containsKey("abc"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddKeyNull() {
        ReducingResult reducingResult = new ReducingResult();
        reducingResult.add(null, 123);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddValueNull() {
        ReducingResult reducingResult = new ReducingResult();
        reducingResult.add("abc", null);
    }

    @Test
    public void testGet() {
        ReducingResult reducingResult = new ReducingResult();
        reducingResult.add("abc", 123);
        assertEquals(reducingResult.get("abc"), 123);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() {
        ReducingResult reducingResult = new ReducingResult();
        reducingResult.get(null);
    }
}
