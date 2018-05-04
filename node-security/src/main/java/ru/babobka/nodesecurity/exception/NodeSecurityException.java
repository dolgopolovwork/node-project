package ru.babobka.nodesecurity.exception;

import java.io.IOException;

/**
 * Created by 123 on 25.04.2018.
 */
public class NodeSecurityException extends IOException {

    private static final long serialVersionUID = -4271551569353065133L;

    public NodeSecurityException(String message) {
        super(message);
    }
}
