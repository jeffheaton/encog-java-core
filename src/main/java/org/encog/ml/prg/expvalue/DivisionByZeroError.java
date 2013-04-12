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
	 * Message with a throwable.
	 * @param msg The message.
	 * @param t The throwable.
	 */
	public DivisionByZeroError(String msg, Throwable t) {
		super(msg, t);
	}

	/**
	 * Just a throwable.
	 * @param t The throwable.
	 */
	public DivisionByZeroError(Throwable t) {
		super(t);
	}

	/**
	 * Just a message.
	 * @param msg The message.
	 */
	public DivisionByZeroError(String msg) {
		super(msg);
	}
}
