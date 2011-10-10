package org.encog.engine.network.activation;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;

/**
 * Linear activation function that bounds the output to [-1,+1].  This
 * activation is typically part of a CPPN neural network, such as 
 * HyperNEAT.
 * 
 * The idea for this activation function was developed by  Ken Stanley, of  
 * the University of Texas at Austin.
 * http://www.cs.ucf.edu/~kstanley/
 */
public class ActivationClippedLinear implements ActivationFunction {
	
	@Override
	public void activationFunction(double[] d, int start, int size) {
		for(int i=0;i<size;i++) {
            if(d[i] < -1.0) {
                d[i] = -1.0;
            }
            if (d[i] > 1.0) {
                d[i] = 1.0;
            }
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
		return new ActivationClippedLinear();
	}

}
