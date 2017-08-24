package ru.babobka.nodeutils.thread;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by 123 on 21.10.2017.
 */
public class ThreadPoolServiceTest {

    private MockThreadPoolService threadPoolService;

    @Before
    public void setUp() {
        threadPoolService = spy(new MockThreadPoolService(1));
    }

    @After
    public void tearDown() {
        threadPoolService.stop();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCores() {
        new MockThreadPoolService(-1);
    }

    @Test
    public void testStop() {
        threadPoolService.stop();
        verify(threadPoolService).stopImpl();
        assertTrue(threadPoolService.isStopped());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testExecuteNullInput() {
        threadPoolService.execute(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testExecuteWasStopped() {
        threadPoolService.stop();
        threadPoolService.execute("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteNoStopNullInput() {
        threadPoolService.executeNoShutDown(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testExecutNoStopWasStopped() {
        threadPoolService.stop();
        threadPoolService.executeNoShutDown("test");
    }

    @Test
    public void testExecute() {
        String input = "test";
        threadPoolService.execute(input);
        verify(threadPoolService).executeImpl(input);
        verify(threadPoolService).shutdown();
    }

    @Test(expected = IllegalStateException.class)
    public void testExecuteBusy() throws InterruptedException {
        String input = "test";
        ThreadPoolService threadPoolService = new ThreadPoolService(1) {
            @Override
            protected void stopImpl() {

            }

            @Override
            protected Serializable executeImpl(Serializable input) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                threadPoolService.execute(input);
            }
        }).start();
        Thread.sleep(200L);
        threadPoolService.execute(input);
    }

    @Test
    public void testExecuteNoStop() {
        String input = "test";
        threadPoolService.executeNoShutDown(input);
        verify(threadPoolService).executeImpl(input);
        assertFalse(threadPoolService.isStopped());
    }

    private static class MockThreadPoolService extends ThreadPoolService {

        public MockThreadPoolService(int cores) {
            super(cores);
        }

        @Override
        protected void stopImpl() {

        }

        @Override
        protected Serializable executeImpl(Serializable input) {
            return null;
        }

    }
}
