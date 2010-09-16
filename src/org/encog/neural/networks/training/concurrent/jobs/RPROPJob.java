package org.encog.neural.networks.training.concurrent.jobs;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;

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
	
	
	
}
