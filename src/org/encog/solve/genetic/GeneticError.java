package org.encog.solve.genetic;

import org.encog.EncogError;

public class GeneticError extends EncogError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5557732297908150500L;

	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public GeneticError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public GeneticError(final Throwable t) {
		super(t);
	}
	
	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param msg
	 *            A message.
	 * @param t
	 * 			The other exception.
	 */
	public GeneticError( final String msg, final Throwable t) {
		super(msg,t);
	}

}
