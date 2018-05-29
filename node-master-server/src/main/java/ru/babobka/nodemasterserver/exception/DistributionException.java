package ru.babobka.nodemasterserver.exception;

public class DistributionException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 471658253997347795L;

    public DistributionException() {
        super();
    }

    public DistributionException(String message) {
        super(message);
    }

    public DistributionException(Throwable cause) {
        super(cause);
    }
}
