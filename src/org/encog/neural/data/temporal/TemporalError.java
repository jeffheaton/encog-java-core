package org.encog.neural.data.temporal;

import org.encog.EncogError;

public class TemporalError extends EncogError {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5534812476029464649L;

	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public TemporalError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public TemporalError(final Throwable t) {
		super(t);
	}	

}
