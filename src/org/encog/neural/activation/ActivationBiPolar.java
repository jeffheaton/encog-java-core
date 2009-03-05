package org.encog.neural.activation;

import org.encog.matrix.BiPolarUtil;
import org.encog.neural.NeuralNetworkError;

public class ActivationBiPolar implements ActivationFunction {

	@Override
	public void activationFunction(double[] d) {
		for(int i=0;i<d.length;i++)
		{
			if( d[i]>0 )
				d[i] = 1;
			else
				d[i] = -1;
		}
		
	}

	@Override
	public void derivativeFunction(double[] d) {
		throw new NeuralNetworkError(
				"Can't use the bipolar activation function "
						+ "where a derivative is required.");
		
	}

}
