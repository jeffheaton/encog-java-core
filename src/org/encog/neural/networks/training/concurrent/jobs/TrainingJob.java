package org.encog.neural.networks.training.concurrent.jobs;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.opencl.EncogCLDevice;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.strategy.end.EndTrainingStrategy;

public abstract class TrainingJob {

	private BasicNetwork network;
	private NeuralDataSet training;
	private final List<Strategy> strategies = new ArrayList<Strategy>();
	private boolean loadToMemory;
	private Train train;
	private Throwable error;
	
	public TrainingJob(BasicNetwork network, NeuralDataSet training, boolean loadToMemory) {
		super();
		this.network = network;
		this.training = training;
		this.loadToMemory = loadToMemory;
	}

	/**
	 * @return the network
	 */
	public BasicNetwork getNetwork() {
		return network;
	}

	/**
	 * @param network the network to set
	 */
	public void setNetwork(BasicNetwork network) {
		this.network = network;
	}

	/**
	 * @return the training
	 */
	public NeuralDataSet getTraining() {
		return training;
	}

	/**
	 * @param training the training to set
	 */
	public void setTraining(NeuralDataSet training) {
		this.training = training;
	}

	/**
	 * @return the loadToMemory
	 */
	public boolean isLoadToMemory() {
		return loadToMemory;
	}

	/**
	 * @param loadToMemory the loadToMemory to set
	 */
	public void setLoadToMemory(boolean loadToMemory) {
		this.loadToMemory = loadToMemory;
	}

	/**
	 * @return the strategies
	 */
	public List<Strategy> getStrategies() {
		return strategies;
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

	/**
	 * @return the train
	 */
	public Train getTrain() {
		return train;
	}

	/**
	 * @param train the train to set
	 */
	public void setTrain(Train train) {
		this.train = train;
	}
	
	public abstract void createTrainer(EncogCLDevice device);
	
	
}
