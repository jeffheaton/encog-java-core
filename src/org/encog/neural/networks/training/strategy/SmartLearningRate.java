package org.encog.neural.networks.training.strategy;

import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartLearningRate implements Strategy {

	private Train train;
	private LearningRate setter;
	private double currentLearningRate;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private long trainingSize;
	private double lastError;
	private boolean ready;
	
	@Override
	public void init(Train train) {
		this.train = train;	
		this.ready = false;
		this.setter = (LearningRate)train;
		this.trainingSize = determineTrainingSize();
		this.currentLearningRate = 1.0/this.trainingSize;
		if( logger.isInfoEnabled() )
		{
		logger.info("Starting learning rate: {}", this.currentLearningRate);
		}	
		this.setter.setLearningRate(this.currentLearningRate);
	}
	
	private long determineTrainingSize()
	{
		long result = 0;
		for(@SuppressWarnings("unused")
		final NeuralDataPair pair: this.train.getTraining())
			result++;
		return result;
	}

	@Override
	public void postIteration() {
		if( this.ready )
		{
			if( this.train.getError()>this.lastError )
			{
				this.currentLearningRate*=0.99;
				this.setter.setLearningRate(this.currentLearningRate);
				if( logger.isInfoEnabled() )
				{
				logger.info("Adjusting learning rate to {}", this.currentLearningRate);
				}
			}
		}
		else
			this.ready = true;
		
	}

	@Override
	public void preIteration() {
		this.lastError = this.train.getError();		
	}



}
