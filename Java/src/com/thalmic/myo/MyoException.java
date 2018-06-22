package com.thalmic.myo;

public class MyoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4201749969607436643L;

	public MyoException() {
		super();
	}

	public MyoException(String message) {
		super(message);
	}

	public MyoException(Throwable cause) {
		super(cause);
	}

	public MyoException(String message, Throwable cause) {
		super(message, cause);
	}

	public MyoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
