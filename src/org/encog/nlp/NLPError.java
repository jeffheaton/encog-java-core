package org.encog.nlp;

import org.encog.EncogError;

public class NLPError extends EncogError {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 8005297054151951786L;

	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public NLPError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public NLPError(final Throwable t) {
		super(t);
	}	
}
