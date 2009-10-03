package org.encog.util.csv;

import org.encog.EncogError;

public class CSVError extends EncogError {
	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public CSVError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public CSVError(final Throwable t) {
		super(t);
	}
}
