package org.encog.app.analyst;

import org.encog.EncogError;

public class AnalystError extends EncogError {

	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public AnalystError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public AnalystError(final Throwable t) {
		super(t);
	}
	
}
