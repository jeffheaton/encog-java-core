package org.encog.neural.networks.layers;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationLinear;

public class NormalizedLayer extends BasicLayer {

	public NormalizedLayer(ActivationFunction thresholdFunction, int neuronCount) {
		super(thresholdFunction, neuronCount);
		// TODO Auto-generated constructor stub
	}
	
	public NormalizedLayer(int neuronCount)
	{
		this(new ActivationLinear(),neuronCount);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 562197138888921746L;

}
