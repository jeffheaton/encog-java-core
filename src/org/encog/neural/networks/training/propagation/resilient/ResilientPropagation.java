package org.encog.neural.networks.training.propagation.resilient;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.PropagationMethod;

public class ResilientPropagation extends Propagation {

	public ResilientPropagation(BasicNetwork network, 
			NeuralDataSet training, double learnRate, double momentum) {
		super(network, new ResilientPropagationMethod(), training, learnRate, momentum);
		// TODO Auto-generated constructor stub
	}

}
