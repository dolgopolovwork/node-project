package ru.babobka.nodeutils.time;

import org.apache.log4j.Logger;
import ru.babobka.nodeutils.time.exception.TimerInvokerException;
import ru.babobka.nodeutils.time.inter.CallableInvoker;
import ru.babobka.nodeutils.time.inter.RunnableInvoker;

/**
 * Created by 123 on 06.08.2018.
 */
public class TimerInvoker {
    private static final Logger logger = Logger.getLogger(TimerInvoker.class);
    private final int maxDelayMillis;

    private TimerInvoker(int maxDelayMillis) {
        if (maxDelayMillis < 0) {
            throw new IllegalArgumentException("maxDelayMillis cannot be negative");
        }
        this.maxDelayMillis = maxDelayMillis;
    }

    public static TimerInvoker create(int maxDelayMillis) {
        return new TimerInvoker(maxDelayMillis);
    }

    public static TimerInvoker createMaxOneSecondDelay() {
        return new TimerInvoker(1_000);
    }

    public void invoke(RunnableInvoker runnable, String operationName) throws Exception {
        Timer timer = new Timer(operationName);
        try {
            runnable.run();
        } finally {
            printIfDelayed(timer);
        }
    }

    public <T> T invoke(CallableInvoker<T> callable, String operationName) {
        Timer timer = new Timer(operationName);
        try {
            return callable.call();
        } catch (Exception e) {
            throw new TimerInvokerException(e);
        } finally {
            printIfDelayed(timer);
        }
    }

    private void printIfDelayed(Timer timer) {
        if (timer.getTimePassed() > maxDelayMillis) {
            logger.warn(timer);
        }
    }
}
