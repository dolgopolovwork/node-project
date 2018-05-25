package ru.babobka.nodeutils.logger;


public interface NodeLogger {
    void info(Object o);

    void info(String message);

    void warning(String message);

    void warning(Exception e);

    void warning(String message, Exception e);

    void debug(String message);

    void error(String message);

    void error(Exception e);

    void error(String message, Exception e);

}
