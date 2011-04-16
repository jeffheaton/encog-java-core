package org.encog.app.quant;

/**
 * Defines an interface for Encog quant tasks.
 * @author jheaton
 *
 */
public interface QuantTask {
	
	/**
	 * Request to stop.
	 */
	void requestStop();
	
	/**
	 * @return Determine if we should stop.
	 */
	boolean shouldStop();

}
