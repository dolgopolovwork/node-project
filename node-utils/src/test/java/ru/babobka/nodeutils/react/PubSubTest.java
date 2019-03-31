package ru.babobka.nodeutils.react;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeutils.func.Subscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;

public class PubSubTest {

    private PubSub<String> pubSub;

    @Before
    public void setUp() {
        pubSub = spy(new PubSub<>());
    }

    @Test
    public void testPublishMainThread() {
        int items = 10;
        Subscriber<String> subscriber = mock(Subscriber.class);
        String subject = "abc";
        pubSub.subscribe(subscriber);
        for (int i = 0; i < items; i++) {
            pubSub.publish(subject);
        }
        verify(subscriber, times(items)).subscribe(subject);
    }

    @Test
    public void testPublishExecutorService() throws InterruptedException {
        int threads = 4;
        ExecutorService threadPool = spy(Executors.newFixedThreadPool(threads));
        Subscriber<String> subscriber = spy(new Subscriber<String>() {
            @Override
            public void subscribe(String item) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //Ignore
                }
            }
        });
        String subject = "abc";
        pubSub.subscribe(subscriber, threadPool);
        for (int i = 0; i < threads; i++) {
            pubSub.publish(subject);
        }
        Thread.sleep(2000);
        verify(subscriber, times(threads)).subscribe(subject);
        verify(threadPool, times(threads)).submit(any(Runnable.class));
        threadPool.shutdownNow();
    }

    @Test
    public void testPublishExecutorServiceComplete() throws InterruptedException {
        int threads = 4;
        ExecutorService threadPool = spy(Executors.newFixedThreadPool(threads));
        Subscriber<String> subscriber = mock(Subscriber.class);
        String subject = "abc";
        pubSub.subscribe(subscriber, threadPool);
        pubSub.close();
        for (int i = 0; i < threads; i++) {
            pubSub.publish(subject);
        }
        //Wait a little
        Thread.sleep(500);
        verify(subscriber, never()).subscribe(subject);
        verify(threadPool).shutdownNow();
        threadPool.shutdownNow();
    }

    @Test
    public void testPublishExecutorServiceCompleteInTheMiddle() throws InterruptedException {
        int threads = 4;
        ExecutorService threadPool = spy(Executors.newFixedThreadPool(threads));
        Subscriber<String> subscriber = mock(Subscriber.class);
        String subject = "abc";
        pubSub.subscribe(subscriber, threadPool);
        for (int i = 0; i < threads; i++) {
            pubSub.publish(subject);
        }
        //Wait a little
        Thread.sleep(500);
        pubSub.close();
        for (int i = 0; i < threads; i++) {
            pubSub.publish(subject);
        }
        verify(subscriber, times(threads)).subscribe(subject);
        verify(threadPool, times(threads)).submit(any(Runnable.class));
        verify(threadPool).shutdownNow();
        threadPool.shutdownNow();
    }


    @Test
    public void testPublishExecutorServiceBadFilter() throws InterruptedException {
        int threads = 4;
        ExecutorService threadPool = spy(Executors.newFixedThreadPool(threads));
        Subscriber<String> subscriber = mock(Subscriber.class);
        String subject = "abc";
        pubSub.subscribe(item -> false
                , subscriber, threadPool);
        for (int i = 0; i < threads; i++) {
            pubSub.publish(subject);
        }

        //Wait a little
        Thread.sleep(500);
        verify(subscriber, never()).subscribe(subject);
        verify(threadPool, never()).submit(any(Runnable.class));
        threadPool.shutdownNow();
    }

    @Test
    public void testPublishExecutorServiceDoubleUse() throws InterruptedException {
        int threads = 4;
        ExecutorService threadPool = spy(Executors.newFixedThreadPool(threads));
        Subscriber<String> subscriber = mock(Subscriber.class);
        String subject = "abc";
        pubSub.subscribe(subscriber, threadPool);
        pubSub.subscribe(subscriber, threadPool);
        for (int i = 0; i < threads; i++) {
            pubSub.publish(subject);
        }
        //Wait a little
        Thread.sleep(500);
        verify(subscriber, times(threads * 2)).subscribe(subject);
        verify(threadPool, times(threads * 2)).submit(any(Runnable.class));
        threadPool.shutdownNow();
    }

    @Test
    public void testPublishMainThreadFilter() {
        int items = 10;
        Subscriber<String> subscriber = mock(Subscriber.class);
        pubSub.subscribe(item -> Integer.parseInt(item) % 2 == 0, subscriber);
        for (int i = 0; i < items; i++) {
            pubSub.publish(String.valueOf(i));
        }
        verify(subscriber, times(items / 2)).subscribe(anyString());
    }

    @Test
    public void testPublishMainThreadOnComplete() {
        int items = 10;
        Subscriber<String> subscriber = mock(Subscriber.class);
        pubSub.close();
        String subject = "abc";
        pubSub.subscribe(subscriber);
        for (int i = 0; i < items; i++) {
            pubSub.publish(subject);
        }
        verify(subscriber, never()).subscribe(subject);
    }

    @Test
    public void testPublishMainThreadTwoSubscribers() {
        int items = 10;
        Subscriber<String> subscriber1 = mock(Subscriber.class);
        Subscriber<String> subscriber2 = mock(Subscriber.class);
        String subject = "abc";
        pubSub.subscribe(subscriber1);
        pubSub.subscribe(subscriber2);
        for (int i = 0; i < items; i++) {
            pubSub.publish(subject);
        }
        verify(subscriber1, times(items)).subscribe(subject);
        verify(subscriber2, times(items)).subscribe(subject);
    }

    @Test
    public void testPublishMainThreadTwoSubscribersOneBadFilter() {
        int items = 10;
        Subscriber<String> subscriber1 = mock(Subscriber.class);
        Subscriber<String> subscriber2 = mock(Subscriber.class);
        String subject = "abc";
        pubSub.subscribe(item ->
                false, subscriber1);
        pubSub.subscribe(subscriber2);
        for (int i = 0; i < items; i++) {
            pubSub.publish(subject);
        }
        verify(subscriber1, never()).subscribe(subject);
        verify(subscriber2, times(items)).subscribe(subject);
    }

    @Test
    public void testPublishMainThreadNotSubscribed() {
        int items = 10;
        Subscriber<String> subscriber = mock(Subscriber.class);
        String subject = "abc";
        for (int i = 0; i < items; i++) {
            pubSub.publish(subject);
        }
        verify(subscriber, never()).subscribe(subject);
    }

    @Test
    public void testPublishMainThreadCompleteInTheMiddle() {
        int items = 10;
        Subscriber<String> subscriber = mock(Subscriber.class);
        pubSub.subscribe(subscriber);
        String subject = "abc";
        for (int i = 0; i < items; i++) {
            pubSub.publish(subject);
        }
        pubSub.close();
        for (int i = 0; i < items; i++) {
            pubSub.publish(subject);
        }
        verify(subscriber, times(items)).subscribe(subject);
    }

    @Test
    public void testPublishMainThreadBadFilter() {
        int items = 10;
        Subscriber<String> subscriber = mock(Subscriber.class);
        String subject = "abc";
        pubSub.subscribe(item -> false, subscriber);
        for (int i = 0; i < items; i++) {
            pubSub.publish(subject);
        }
        verify(subscriber, never()).subscribe(subject);
    }

    @Test
    public void testPublishMixed() throws InterruptedException {
        int threads = 4;
        ExecutorService threadPool = spy(Executors.newFixedThreadPool(threads));
        Subscriber<String> poolSubscriber = mock(Subscriber.class);
        Subscriber<String> mainSubscriber = mock(Subscriber.class);
        String subject = "abc";
        pubSub.subscribe(poolSubscriber, threadPool);
        pubSub.subscribe(mainSubscriber);
        for (int i = 0; i < threads; i++) {
            pubSub.publish(subject);
        }
        //Wait a little
        Thread.sleep(500);
        verify(mainSubscriber, times(threads)).subscribe(subject);
        verify(poolSubscriber, times(threads)).subscribe(subject);
        verify(threadPool, times(threads)).submit(any(Runnable.class));
        threadPool.shutdownNow();
    }
}
