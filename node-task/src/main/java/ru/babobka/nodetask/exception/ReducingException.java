package ru.babobka.nodetask.exception;

public class ReducingException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -3603894271678084823L;

    public ReducingException() {
        super();
    }

    public ReducingException(String message) {
        super(message);
    }

    public ReducingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReducingException(Throwable cause) {
        super(cause);
    }
}
