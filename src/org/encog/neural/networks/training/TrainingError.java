package org.encog.neural.networks.training;

import org.encog.neural.NeuralNetworkError;

/**
 * Thrown when a training error occurs.
 *
 */
public class TrainingError extends NeuralNetworkError {

	/**
	 * The serial id. 
	 */
	private static final long serialVersionUID = 9138367057650889570L;

	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public TrainingError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public TrainingError(final Throwable t) {
		super(t);
	}	
}
