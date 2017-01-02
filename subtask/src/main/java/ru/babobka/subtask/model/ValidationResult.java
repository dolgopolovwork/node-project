package ru.babobka.subtask.model;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by dolgopolov.a on 08.07.15.
 */
public class ValidationResult {

	private final String message;

	private final boolean valid;

	public ValidationResult(boolean valid) {
		this.message = null;
		this.valid = valid;
	}

	public ValidationResult(String message, boolean valid) {
		this.message = message;
		this.valid = valid;
	}

	public ValidationResult(Exception e, boolean valid) {
		this(getStringFromException(e), valid);
	}

	public String getMessage() {
		return message;
	}

	public boolean isValid() {
		return valid;
	}

	private static String getStringFromException(Exception ex) {
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
}
