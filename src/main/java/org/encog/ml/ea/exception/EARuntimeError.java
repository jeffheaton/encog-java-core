package org.encog.ml.ea.exception;

public class EARuntimeError extends EAError {
	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;

	public EARuntimeError(String msg, Throwable t) {
		super(msg, t);
	}

	public EARuntimeError(Throwable t) {
		super(t);
	}

	public EARuntimeError(String msg) {
		super(msg);
	}
}
