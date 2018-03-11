package ru.babobka.vsjws.model.http;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class HttpSessionTest {

    private static final int SESSION_TIMEOUT = 1;

    private static final int OVERTIME = SESSION_TIMEOUT * 1050;

    private static final String SESSION_ID = UUID.randomUUID().toString();

    private HttpSession session;

    @Before
    public void setUp() {
        session = new HttpSession(SESSION_TIMEOUT);
        session.create(SESSION_ID);
    }

    @Test
    public void testCreate() throws InterruptedException {
        assertNotNull(session.get(SESSION_ID));
        Thread.sleep(OVERTIME);
        assertNull(session.get(SESSION_ID));
    }

    @Test
    public void testPut() {
        String key = "key", value = "value";
        session.get(SESSION_ID).put(key, value);
        assertEquals(session.get(SESSION_ID).get(key), value);
    }

    @Test
    public void testExists() throws InterruptedException {
        assertTrue(session.exists(SESSION_ID));
        Thread.sleep(OVERTIME);
        assertFalse(session.exists(SESSION_ID));
    }

}