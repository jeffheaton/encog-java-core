package org.encog.neural.networks.training;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.Network;

public abstract class BasicTraining implements Train {

	private List<Strategy> strategies = new ArrayList<Strategy>();
	/**
	 * The training data.
	 */
	private NeuralDataSet training;
	
	private double error;
	
	public void addStrategy(Strategy strategy) {
		strategy.init(this);
		this.strategies.add(strategy);		
	}
	
	
	
	public List<Strategy> getStrategies() {
		return strategies;
	}

	public NeuralDataSet getTraining() {
		return training;
	}



	public void setTraining(NeuralDataSet training) {
		this.training = training;
	}



	public void preIteration()
	{
		for( Strategy strategy: this.strategies )
		{
			strategy.preIteration();
		}
	}
	
	public void postIteration()
	{
		for( Strategy strategy: this.strategies )
		{
			strategy.postIteration();
		}
	}



	public double getError() {
		return error;
	}



	public void setError(double error) {
		this.error = error;
	}
	
	
	
	
	

}
