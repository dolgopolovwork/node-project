package ru.babobka.nodeutils.thread;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

/**
 * Created by 123 on 18.09.2017.
 */
public abstract class CyclicThread extends Thread {

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    @Override
    public final void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    sleep(sleepMillis());
                    onCycle();
                } catch (RuntimeException e) {
                    logger.error(e);
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
            logger.error(e);
        }
    }

    public void onExit() {
        //do nothing by default
    }

    public abstract void onCycle();

}
