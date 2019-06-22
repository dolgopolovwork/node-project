package ru.babobka.nodeconfigs.exception;

import lombok.NonNull;

public class EnvConfigCreationException extends Exception {

    private static final long serialVersionUID = -5140190836361036771L;

    public EnvConfigCreationException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
