package ru.babobka.factor.exception;

/**
 * Created by dolgopolov.a on 23.11.15.
 */
public class InfinityPointException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7611051195543283809L;

	public InfinityPointException() {
    }

    public InfinityPointException(String message) {
        super(message);
    }
}