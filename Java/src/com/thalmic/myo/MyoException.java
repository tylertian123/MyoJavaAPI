package com.thalmic.myo;

/**
 * An exception thrown to indicate something has gone wrong with the Myo components ({@link Myo} and {@link Hub}).
 * Typically thrown when attempting to modify a released {@link Hub} object, or when the {@link Hub} constructor
 * fails.
 *
 */
public class MyoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4201749969607436643L;

	/** Constructs a new Myo exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
	public MyoException() {
		super();
	}

	/** Constructs a new Myo exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
	public MyoException(String message) {
		super(message);
	}
	
	/**
	 * Constructs a new Myo exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
	 */
	public MyoException(Throwable cause) {
		super(cause);
	}

	/**
     * Constructs a new Myo exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
	public MyoException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * Constructs a new Myo exception with the specified detail
     * message, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     *
     * @param  message the detail message.
     * @param cause the cause.  (A {@code null} value is permitted,
     * and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression whether or not suppression is enabled
     *                          or disabled
     * @param writableStackTrace whether or not the stack trace should
     *                           be writable
     */
	public MyoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
