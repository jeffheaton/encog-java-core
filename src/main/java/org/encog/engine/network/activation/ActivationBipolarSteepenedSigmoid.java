package org.encog.engine.network.activation;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;

/**
 * The bipolar sigmoid activation function is like the regular sigmoid activation function,
 * except Bipolar sigmoid activation function. TheOutput range is -1 to 1 instead of the more normal 0 to 1.
 * 
 * This activation is typically part of a CPPN neural network, such as 
 * HyperNEAT.
 * 
 * The idea for this activation function was developed by  Ken Stanley, of  
 * the University of Texas at Austin.
 * http://www.cs.ucf.edu/~kstanley/
 */
public class ActivationBipolarSteepenedSigmoid implements ActivationFunction {
	
	@Override
	public void activationFunction(double[] d, int start, int size) {
		for(int i=start;i<start+size;i++) {
			d[i] = (2.0 / (1.0 + Math.exp(-4.9 * d[i]))) - 1.0;
		}		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double derivativeFunction(double b, double a) {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasDerivative() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] getParams() {
		return ActivationLinear.P;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParam(int index, double value) {		
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getParamNames() {
		return ActivationLinear.N;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ActivationFunction clone() {
		return new ActivationBipolarSteepenedSigmoid();
	}

}
