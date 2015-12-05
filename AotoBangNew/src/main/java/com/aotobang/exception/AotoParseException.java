package com.aotobang.exception;

public class AotoParseException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AotoParseException() {
	}

	public AotoParseException(String message) {
		super(message);
	}

	public AotoParseException(Throwable cause) {
		super(cause);
	}

	public AotoParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
