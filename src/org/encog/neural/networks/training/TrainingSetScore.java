package org.encog.neural.networks.training;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;

public class TrainingSetScore implements CalculateScore {

	public final NeuralDataSet training;
	
	public TrainingSetScore(NeuralDataSet training)
	{
		this.training = training;
	}
	
	@Override
	public double calculateScore(BasicNetwork network) {
		return network.calculateError(this.training);
	}
	
	public boolean shouldMinimize()
	{
		return true;
	}

}
