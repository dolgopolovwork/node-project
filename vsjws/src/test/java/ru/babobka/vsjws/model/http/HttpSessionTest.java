package ru.babobka.vsjws.model.http;

import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpSessionTest {

    private static final int SESSION_TIMEOUT = 1;
    private static final int OVERTIME = SESSION_TIMEOUT * 1050;
    private static final String SESSION_ID = UUID.randomUUID().toString();
    private HttpSession session;
    private HttpRequest httpRequest;
    private InetAddress address;

    @Before
    public void setUp() {
        httpRequest = mock(HttpRequest.class);
        address = mock(InetAddress.class);
        when(httpRequest.getAddress()).thenReturn(address);
        session = new HttpSession(SESSION_TIMEOUT);
        session.create(SESSION_ID, httpRequest);
    }

    @Test
    public void testCreate() throws InterruptedException {
        assertNotNull(session.getData(SESSION_ID));
        Thread.sleep(OVERTIME);
        assertNull(session.getData(SESSION_ID));
    }

    @Test
    public void testPut() {
        String key = "key", value = "value";
        session.getData(SESSION_ID).put(key, value);
        assertEquals(session.getData(SESSION_ID).get(key), value);
    }

    @Test
    public void testExists() throws InterruptedException {
        assertTrue(session.exists(SESSION_ID));
        Thread.sleep(OVERTIME);
        assertFalse(session.exists(SESSION_ID));
    }

}