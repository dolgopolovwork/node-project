package ru.babobka.container;

public class ContainerStrategyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 471658253997347795L;

	public ContainerStrategyException() {
		super();
	}

	public ContainerStrategyException(String message) {
		super(message);
	}

	public ContainerStrategyException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContainerStrategyException(Throwable cause) {
		super(cause);
	}
}
