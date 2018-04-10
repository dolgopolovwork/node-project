package ru.babobka.nodeclient;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by 123 on 31.03.2018.
 */
public class ClientTest {

    private ExecutorService executorService;

    @Before
    public void setUp() {
        executorService = mock(ExecutorService.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoHost() {
        new Client("", 123, executorService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullHost() {
        new Client(null, 123, executorService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadPort() {
        new Client("google.com", -1, executorService);
    }

    @Test
    public void testClose() {
        new Client("google.com", 123, executorService).close();
        verify(executorService).shutdownNow();
    }
}
