package org.encog.neural.networks.training.concurrent;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.strategy.end.EndTrainingStrategy;

public class TrainingJob {
	
	private final BasicNetwork network;
	private final Train train;
	private Throwable error;
	
	/**
	 * @param network
	 * @param train
	 */
	public TrainingJob(BasicNetwork network, Train train) {
		super();
		this.network = network;
		this.train = train;
	}

	/**
	 * @return the network
	 */
	public BasicNetwork getNetwork() {
		return network;
	}

	/**
	 * @return the train
	 */
	public Train getTrain() {
		return train;
	}

	/**
	 * @return the error
	 */
	public Throwable getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(Throwable error) {
		this.error = error;
	}
	
	public boolean shouldContinue()
	{
		for( Strategy strategy : this.train.getStrategies() )
		{
			if( strategy instanceof EndTrainingStrategy )
			{
				EndTrainingStrategy end = (EndTrainingStrategy)strategy;
				
				if( end.shouldStop() )
					return false;
			}
		}
		return true;
	}

	
	
}
