package org.encog.neural.networks.training.pnn;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.pnn.BasicPNN;

public class TrainBasicPNN {

	private final BasicPNN network;
	private final NeuralDataSet training;
	
	public TrainBasicPNN(BasicPNN network, NeuralDataSet training)
	{
		this.network = network;
		this.training = training;
	}	
	
	
}
