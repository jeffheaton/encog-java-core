package org.encog.neural.data.market;

import org.encog.neural.data.temporal.TemporalError;

public class MarketError extends TemporalError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9199552396430520659L;

	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public MarketError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public MarketError(final Throwable t) {
		super(t);
	}	

}
