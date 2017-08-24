package ru.babobka.nodemasterserver.exception;

/**
 * Created by 123 on 17.10.2017.
 */
public class TaskExecutionException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 471658253997347795L;

    public TaskExecutionException() {
        super();
    }

    public TaskExecutionException(String message) {
        super(message);
    }

    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskExecutionException(Throwable cause) {
        super(cause);
    }
}
