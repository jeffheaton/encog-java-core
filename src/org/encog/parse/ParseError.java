package org.encog.parse;

import org.encog.EncogError;

public class ParseError extends EncogError {
	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public ParseError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public ParseError(final Throwable t) {
		super(t);
	}
}
