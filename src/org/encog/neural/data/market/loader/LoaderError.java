package org.encog.neural.data.market.loader;

import org.encog.EncogError;
import org.encog.neural.data.market.MarketError;

public class LoaderError extends MarketError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4525043656696667974L;

	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public LoaderError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public LoaderError(final Throwable t) {
		super(t);
	}	

}
