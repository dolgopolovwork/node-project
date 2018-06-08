package ru.babobka.nodeslaveserver.exception;

import java.io.IOException;

public class SlaveStartupException extends IOException {

    /**
     *
     */
    private static final long serialVersionUID = -3603894271678084823L;

    public SlaveStartupException(String message) {
        super(message);
    }

}
