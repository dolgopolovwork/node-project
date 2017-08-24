package ru.babobka.nodetask.exception;

/**
 * Created by 123 on 19.08.2017.
 */
public class TaskNotFoundException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -3603894271678084823L;

    public TaskNotFoundException() {
        super();
    }

    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskNotFoundException(Throwable cause) {
        super(cause);
    }
}
