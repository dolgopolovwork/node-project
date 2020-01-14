package ru.babobka.nodeutils.thread;

import org.apache.log4j.Logger;

/**
 * Created by 123 on 18.09.2017.
 */
public abstract class CyclicThread extends Thread {

    private static final Logger logger = Logger.getLogger(CyclicThread.class);

    @Override
    public final void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    sleep(sleepMillis());
                    onCycle();
                } catch (RuntimeException e) {
                    logger.error("exception thrown", e);
                }
            }
        } finally {
            onExit();
        }
    }

    public int sleepMillis() {
        return 0;
    }

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            interrupt();
            logger.warn(e);
        }
    }

    public void onExit() {
        //do nothing by default
    }

    public abstract void onCycle();

}
