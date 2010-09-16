package org.encog.neural.networks.training.concurrent.jobs;

import org.encog.engine.opencl.EncogCLDevice;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

public class RPROPJob extends TrainingJob {
	
	private double initialUpdate;
	private double maxStep;
	
	public RPROPJob(BasicNetwork network, NeuralDataSet training, boolean loadToMemory,
			double initialUpdate, double maxStep) {
		super(network,training,loadToMemory);
		this.initialUpdate = initialUpdate;
		this.maxStep = maxStep;
	}

	/**
	 * @return the initialUpdate
	 */
	public double getInitialUpdate() {
		return initialUpdate;
	}

	/**
	 * @param initialUpdate the initialUpdate to set
	 */
	public void setInitialUpdate(double initialUpdate) {
		this.initialUpdate = initialUpdate;
	}

	/**
	 * @return the maxStep
	 */
	public double getMaxStep() {
		return maxStep;
	}

	/**
	 * @param maxStep the maxStep to set
	 */
	public void setMaxStep(double maxStep) {
		this.maxStep = maxStep;
	}
	
	@Override
	public void createTrainer(EncogCLDevice device) {
		Train train = new ResilientPropagation(
				getNetwork(),
				getTraining(),
				device,
				this.getInitialUpdate(),
				this.getMaxStep());
		
		for(Strategy strategy : this.getStrategies() ) {
			train.addStrategy(strategy);
		}
		
		this.setTrain(train);
	}
	
	
}
