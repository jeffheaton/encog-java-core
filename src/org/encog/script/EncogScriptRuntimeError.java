package org.encog.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EncogScriptRuntimeError extends EncogScriptError {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5909341149180956178L;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a message exception.
	 * 
	 * @param msg
	 *            The exception message.
	 */
	public EncogScriptRuntimeError(final String msg) {
		super(msg);
	}

	/**
	 * Construct an exception that holds another exception.
	 * 
	 * @param t
	 *            The other exception.
	 */
	public EncogScriptRuntimeError(final Throwable t) {
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
	public EncogScriptRuntimeError( final String msg, final Throwable t) {
		super(msg,t);
	}
}
