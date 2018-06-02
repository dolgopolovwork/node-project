package ru.babobka.nodeslaveserver.exception;

import java.io.IOException;

public class AuthFailException extends IOException {

    /**
     *
     */
    private static final long serialVersionUID = -3603894271678084823L;

    public AuthFailException(String message) {
        super(message);
    }

}
