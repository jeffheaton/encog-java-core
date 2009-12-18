package org.encog.neural.networks.training.propagation.gradient;

import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.propagation.multi.MPROPWorker;
import org.encog.util.ErrorCalculation;

import java.util.HashMap;
import java.util.Map;

public class CalculateGradient {

	private GradientUtil gradient;
	
	public CalculateGradient(BasicNetwork network) {
		this.gradient = new GradientUtil(network);
	}
	
	public void calculate(NeuralDataSet training, double[] weights)
	{
		this.gradient.calculate(training, weights);
	}

	public double[] getErrors() {
		return this.gradient.getErrors();
	}

	public double getError() {
		return this.gradient.getError();
	}

	
}
