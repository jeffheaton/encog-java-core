package org.encog.ml.bayesian;

import org.encog.EncogError;
import org.encog.util.logging.EncogLogging;

public class BayesianError extends EncogError {
	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public BayesianError(final String msg) {
		super(msg);
		EncogLogging.log(EncogLogging.LEVEL_ERROR, msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public BayesianError(final Throwable t) {
		super(t);
		EncogLogging.log(EncogLogging.LEVEL_ERROR, t);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param msg
	 *            A message.
	 * @param t
	 *            The other exception.
	 */
	public BayesianError(final String msg, final Throwable t) {
		super(msg, t);
		EncogLogging.log(EncogLogging.LEVEL_ERROR, msg);
		EncogLogging.log(EncogLogging.LEVEL_ERROR, t);
	}

}
