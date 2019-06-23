package ru.babobka.nodeserials.exception;

import java.io.IOException;

public class NodeSerializationException extends IOException {
    private static final long serialVersionUID = 1530882901899037513L;

    public NodeSerializationException(String message, Exception e) {
        super(message, e);
    }
}
