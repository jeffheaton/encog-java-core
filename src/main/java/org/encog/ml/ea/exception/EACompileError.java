package org.encog.ml.ea.exception;

public class EACompileError extends EAError {
	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;

	public EACompileError(String msg, Throwable t) {
		super(msg, t);
	}

	public EACompileError(Throwable t) {
		super(t);
	}

	public EACompileError(String msg) {
		super(msg);
	}
}
