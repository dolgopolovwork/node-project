package ru.babobka.nodeslaveserver.exception;

import java.io.IOException;

public class AuthFailException extends IOException {

    /**
     *
     */
    private static final long serialVersionUID = -3603894271678084823L;

    public AuthFailException() {
        super();
    }

    public AuthFailException(String message) {
        super(message);
    }

    public AuthFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthFailException(Throwable cause) {
        super(cause);
    }
}
