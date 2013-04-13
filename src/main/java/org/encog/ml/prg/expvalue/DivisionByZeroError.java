package org.encog.ml.prg.expvalue;

import org.encog.ml.ea.exception.EARuntimeError;

/**
 * A division by zero.
 */
public class DivisionByZeroError extends EARuntimeError {
	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;

	public DivisionByZeroError() {
		this("Division by zero");
	}

	/**
	 * Just a message.
	 * 
	 * @param msg
	 *            The message.
	 */
	public DivisionByZeroError(final String msg) {
		super(msg);
	}

	/**
	 * Message with a throwable.
	 * 
	 * @param msg
	 *            The message.
	 * @param t
	 *            The throwable.
	 */
	public DivisionByZeroError(final String msg, final Throwable t) {
		super(msg, t);
	}

	/**
	 * Just a throwable.
	 * 
	 * @param t
	 *            The throwable.
	 */
	public DivisionByZeroError(final Throwable t) {
		super(t);
	}
}
