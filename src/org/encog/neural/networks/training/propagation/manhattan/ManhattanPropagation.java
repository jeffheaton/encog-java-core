package org.encog.neural.networks.training.propagation.manhattan;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.Propagation;

public class ManhattanPropagation extends Propagation {

	public ManhattanPropagation(BasicNetwork network, 
			NeuralDataSet training, double learnRate, double momentum) {
		super(network, new ManhattanPropagationMethod(), training, learnRate, momentum);
		// TODO Auto-generated constructor stub
	}

}
