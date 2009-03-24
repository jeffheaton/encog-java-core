package org.encog.neural.networks.layers;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextLayer extends BasicLayer {

	private double[] context;
	
	final Logger logger = LoggerFactory.getLogger(ContextLayer.class);
	
	public ContextLayer(ActivationFunction thresholdFunction, int neuronCount) {
		super(thresholdFunction, neuronCount);
		context = new double[neuronCount];
	}
	
	public ContextLayer(int neuronCount) {
		this(new ActivationTANH(), neuronCount);
	}

	public void compute(final NeuralData pattern)
	{
		for(int i = 0; i<pattern.size();i++)
		{
			this.context[i] = pattern.getData(i);
		}
		
		if( logger.isDebugEnabled() ) {
			logger.debug("Updated ContextLayer to {}", pattern);
		}
		
				
		// apply the activation function
		this.getActivationFunction().activationFunction(pattern.getData());
	}
	
	public void recur(NeuralData input) {
		for(int i=0;i<input.size();i++)
		{
			input.setData(i, input.getData(i)+this.context[i]);
		}
	}
	
	
	

}
