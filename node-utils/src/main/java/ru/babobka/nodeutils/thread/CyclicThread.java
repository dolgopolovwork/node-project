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
                    sleep();
                    onAwake();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (RuntimeException e) {
                    logger.error(e);
                }
            }
        } finally {
            onExit();
        }
    }

    public void sleep() throws InterruptedException {
        //do nothing by default
    }

    public void onExit() {
        //do nothing by default
    }

    public abstract void onAwake();

}
