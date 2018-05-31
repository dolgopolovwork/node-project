package ru.babobka.nodeutils.container;

public class ContainerException extends RuntimeException {

    private static final long serialVersionUID = 471658253997347795L;

    public ContainerException(String message) {
        super(message);
    }

    public ContainerException(Throwable cause) {
        super(cause);
    }
}
