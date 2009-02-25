package org.encog.neural.activation;

import org.encog.neural.NeuralNetworkError;

public class ActivationSoftMax implements ActivationFunction {

	@Override
	public void activationFunction(double[] d) {
                
        double sum = 0;
        for (int i = 0; i < d.length; i++) {
            d[i] = Math.exp(d[i]);
            sum += d[i];
        }
        for (int i = 0; i < d.length; i++) {
            d[i] = d[i] / sum;
        }
	}

	@Override
	public void derivativeFunction(double[] d) {
		throw new NeuralNetworkError(
				"Can't use the softmax activation function "
						+ "where a derivative is required.");
	}

}
