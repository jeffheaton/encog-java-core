package org.encog.neural.data;

/**
 * NeuralNetworkError: Used by the neural network classes to 
 * indicate an error.
 */
public class NeuralDataError extends RuntimeException {
	/**
	 * Serial id for this class.
	 */
	private static final long serialVersionUID = 7167228729133120101L;

	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public NeuralDataError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public NeuralDataError(final Throwable t) {
		super(t);
	}
}
