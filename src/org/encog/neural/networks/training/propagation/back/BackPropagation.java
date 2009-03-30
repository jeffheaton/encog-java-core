package org.encog.neural.networks.training.propagation.back;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.PropagationMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackPropagation extends Propagation {


	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	public BackPropagation(BasicNetwork network,NeuralDataSet training,
			double learningRate, double momentum) {
		super(new BackPropagationMethod(), network, learningRate, momentum);

	}
	
}
