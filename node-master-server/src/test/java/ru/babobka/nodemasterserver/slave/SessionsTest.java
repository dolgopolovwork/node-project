package ru.babobka.nodemasterserver.slave;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 19.05.2018.
 */
public class SessionsTest {

    private Sessions sessions = new Sessions();

    @Before
    public void setUp() {
        sessions.clear();
    }

    @Test
    public void testPut() {
        assertTrue(sessions.put("abc"));
        assertTrue(sessions.put("xyz"));
    }

    @Test
    public void testPutContains() {
        assertTrue(sessions.put("abc"));
        assertFalse(sessions.put("abc"));
    }
}
