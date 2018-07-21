package ru.babobka.nodeutils.time;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.time.exception.TimeInvokerException;
import ru.babobka.nodeutils.time.inter.CallableInvoker;
import ru.babobka.nodeutils.time.inter.RunnableInvoker;

/**
 * Created by 123 on 06.08.2018.
 */
public class TimerInvoker {

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

    public void invoke(RunnableInvoker runnable, String operationName) {
        Timer timer = new Timer(operationName);
        try {
            runnable.run();
        } catch (Exception e) {
            throw new TimeInvokerException(e);
        } finally {
            printIfDelayed(timer);
        }
    }

    public <T> T invoke(CallableInvoker<T> callable, String operationName) {
        Timer timer = new Timer(operationName);
        try {
            return callable.call();
        } catch (Exception e) {
            throw new TimeInvokerException(e);
        } finally {
            printIfDelayed(timer);
        }
    }

    private void printIfDelayed(Timer timer) {
        if (timer.getTimePassed() > maxDelayMillis) {
            LazyLogger.logger.warning(timer.toString());
        }
    }

    private static class LazyLogger {
        private static NodeLogger logger = Container.getInstance().get(NodeLogger.class);
    }
}
