package ru.babobka.nodeutils.time;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by 123 on 02.06.2018.
 */
public class TimerTest {

    @Test
    public void testNoArgs() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Timer timer = new Timer();
        int sleepMillis = 100;
        Thread.sleep(sleepMillis);
        assertNull(timer.getTitle());
        assertTrue(startTime <= timer.getStartTime());
        assertTrue(timer.getTimePassed() >= sleepMillis);
    }


    @Test
    public void testHasTitle() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        String title = "abc";
        Timer timer = new Timer(title);
        int sleepMillis = 100;
        Thread.sleep(sleepMillis);
        assertEquals(timer.getTitle(), title);
        assertTrue(startTime <= timer.getStartTime());
        assertTrue(timer.getTimePassed() >= sleepMillis);
    }
}
