package com.thalmic.myo;

public class JNIException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8544388924714627158L;

	public JNIException() {
		super();
	}

	public JNIException(String message) {
		super(message);
	}

	public JNIException(Throwable cause) {
		super(cause);
	}

	public JNIException(String message, Throwable cause) {
		super(message, cause);
	}

	public JNIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
