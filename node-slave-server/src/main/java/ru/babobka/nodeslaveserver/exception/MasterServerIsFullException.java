package ru.babobka.nodeslaveserver.exception;

public class MasterServerIsFullException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -3603894271678084823L;

    public MasterServerIsFullException() {
        super();
    }

    public MasterServerIsFullException(String message) {
        super(message);
    }

    public MasterServerIsFullException(String message, Throwable cause) {
        super(message, cause);
    }

    public MasterServerIsFullException(Throwable cause) {
        super(cause);
    }
}
