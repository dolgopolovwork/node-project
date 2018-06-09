package ru.babobka.nodeslaveserver.exception;

/**
 * Created by 123 on 09.06.2018.
 */
public class SlaveAuthException extends SlaveStartupException {
    private static final long serialVersionUID = 2743470900041934266L;

    public SlaveAuthException(String message) {
        super(message);
    }
}
