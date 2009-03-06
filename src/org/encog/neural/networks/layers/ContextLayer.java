package org.encog.neural.networks.layers;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;

public class ContextLayer extends BasicLayer {

	private double[] context;
	
	public ContextLayer(ActivationFunction thresholdFunction, int neuronCount) {
		super(thresholdFunction, neuronCount);
		context = new double[neuronCount];
	}
	
	public NeuralData compute(final NeuralData pattern)
	{
		NeuralData result = new BasicNeuralData(getNeuronCount());
		
		//NeuralData result = getNext().compute(pattern);
		
		// apply the activation function
		this.getActivationFunction().activationFunction(result.getData());

		return result;
	}

}
