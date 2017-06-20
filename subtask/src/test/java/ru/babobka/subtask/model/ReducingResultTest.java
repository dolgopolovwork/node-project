package ru.babobka.subtask.model;

import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by 123 on 14.06.2017.
 */
public class ReducingResultTest {

    @Test
    public void testAddNull() {
        try {
            new ReducingResult().add(null);
            fail();
        } catch (IllegalArgumentException e) {

        }
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

    @Test
    public void testAddKeyNull() {
        try {
            ReducingResult reducingResult = new ReducingResult();
            reducingResult.add(null, 123);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testAddValueNull() {
        try {
            ReducingResult reducingResult = new ReducingResult();
            reducingResult.add("abc", null);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testGet() {
        ReducingResult reducingResult = new ReducingResult();
        reducingResult.add("abc", 123);
        assertEquals(reducingResult.get("abc"), 123);
    }

    @Test
    public void testGetNull() {
        try {
            ReducingResult reducingResult = new ReducingResult();
            reducingResult.get(null);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }


}
