package ru.babobka.nodeutils.container;

public class ContainerException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 471658253997347795L;

    public ContainerException() {
        super();
    }

    public ContainerException(String message) {
        super(message);
    }

    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerException(Throwable cause) {
        super(cause);
    }
}
