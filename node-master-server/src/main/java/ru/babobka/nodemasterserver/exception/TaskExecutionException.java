package ru.babobka.nodemasterserver.exception;

import ru.babobka.nodeserials.enumerations.ResponseStatus;

/**
 * Created by 123 on 17.10.2017.
 */
public class TaskExecutionException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 471658253997347795L;

    private final ResponseStatus executionStatus;

    public TaskExecutionException(ResponseStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    public TaskExecutionException(String message, ResponseStatus executionStatus) {
        super(message);
        this.executionStatus = executionStatus;
    }

    public TaskExecutionException(String message, ResponseStatus executionStatus, Throwable cause) {
        super(message, cause);
        this.executionStatus = executionStatus;
    }

    public TaskExecutionException(ResponseStatus executionStatus, Throwable cause) {
        super(cause);
        this.executionStatus = executionStatus;
    }

    public ResponseStatus getExecutionStatus() {
        return executionStatus;
    }
}
