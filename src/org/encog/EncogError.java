package org.encog;

public class EncogError extends RuntimeException {

	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public EncogError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public EncogError(final Throwable t) {
		super(t);
	}	
	
}
