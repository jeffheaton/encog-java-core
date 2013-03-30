package org.encog.ml.ea.exception;

import org.encog.EncogError;

public class EAError extends EncogError {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;

	public EAError(String msg, Throwable t) {
		super(msg, t);
	}

	public EAError(Throwable t) {
		super(t);
	}

	public EAError(String msg) {
		super(msg);
	}

}
