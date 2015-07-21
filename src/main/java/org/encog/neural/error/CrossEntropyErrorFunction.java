package org.encog.neural.error;

/**
 * Implements a cross entropy error function.  This can be used with backpropagation to
 * sometimes provide better performance than the standard linear error function.
 * @author jheaton
 *
 */
public class CrossEntropyErrorFunction implements ErrorFunction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void calculateError(double[] ideal, double[] actual, double[] error) {
		for(int i=0;i<actual.length;i++) {
			error[i] = -ideal[i] * Math.log(actual[i]) + (1-ideal[i])*Math.log(1-actual[i]); 
		}
	}

}
