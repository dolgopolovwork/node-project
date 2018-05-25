package ru.babobka.nodeutils.logger;

import ru.babobka.nodeutils.util.TextUtil;

/**
 * Created by 123 on 25.05.2018.
 */
public class DummyNodeLogger implements NodeLogger {

    private static final String INFO = "info:";
    private static final String WARNING = "warning:";
    private static final String DEBUG = "debug:";
    private static final String ERROR = "error:";

    @Override
    public synchronized void info(Object o) {
        System.out.println(INFO + o);
    }

    @Override
    public synchronized void info(String message) {
        System.out.println(INFO + message);
    }

    @Override
    public synchronized void warning(String message) {
        System.out.println(WARNING + message);
    }

    @Override
    public synchronized void warning(Exception e) {
        System.out.println(WARNING + TextUtil.getStringFromException(e));
    }

    @Override
    public synchronized void warning(String message, Exception e) {
        System.out.println(WARNING + message + "; " + TextUtil.getStringFromException(e));
    }

    @Override
    public synchronized void debug(String message) {
        System.out.println(DEBUG + message);
    }

    @Override
    public synchronized void error(String message) {
        System.out.println(ERROR + message);
    }

    @Override
    public synchronized void error(Exception e) {
        System.out.println(ERROR + TextUtil.getStringFromException(e));
    }

    @Override
    public synchronized void error(String message, Exception e) {
        System.out.println(ERROR + message + "; " + TextUtil.getStringFromException(e));

    }
}
