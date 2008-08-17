package org.encog.util.db;

import org.encog.EncogError;

public class DBError extends EncogError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6101412446626697079L;

	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public DBError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public DBError(final Throwable t) {
		super(t);
	}	
}
