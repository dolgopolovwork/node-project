package ru.babobka.nodeutils.thread;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.key.UtilKey;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 21.10.2017.
 */
public class ThreadPoolServiceTest {

    private MockThreadPoolService threadPoolService;

    @Before
    public void setUp() {
        Container.getInstance().put(UtilKey.SERVICE_THREAD_POOL, mock(ExecutorService.class));
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

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteNoStopNullInput() {
        threadPoolService.execute(null);
    }

    @Test
    public void testExecuteWasStopped() {
        threadPoolService.stop();
        Serializable stoppedResponse = mock(Serializable.class);
        doReturn(stoppedResponse).when(threadPoolService).getStoppedResponse();
        assertEquals(stoppedResponse, threadPoolService.execute("test"));
    }

    @Test
    public void testExecute() {
        String input = "test";
        threadPoolService.execute(input);
        verify(threadPoolService).executeImpl(input);
    }

    @Test(expected = IllegalStateException.class)
    public void testExecuteBusy() throws InterruptedException {
        String input = "test";
        ThreadPoolService threadPoolService = new ThreadPoolService(1) {
            @Override
            protected void stopImpl() {

            }

            @Override
            protected Serializable getStoppedResponse() {
                return null;
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
        new Thread(() -> threadPoolService.execute(input)).start();
        Thread.sleep(200L);
        threadPoolService.execute(input);
    }

    private static class MockThreadPoolService extends ThreadPoolService<Serializable, Serializable> {

        public MockThreadPoolService(int cores) {
            super(cores);
        }

        @Override
        protected void stopImpl() {

        }

        @Override
        protected Serializable getStoppedResponse() {
            return null;
        }

        @Override
        protected Serializable executeImpl(Serializable input) {
            return null;
        }

    }
}
