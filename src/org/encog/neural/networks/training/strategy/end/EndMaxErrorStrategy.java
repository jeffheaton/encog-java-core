package org.encog.neural.networks.training.strategy.end;

import org.encog.neural.networks.training.Train;

public class EndMaxErrorStrategy implements EndTrainingStrategy {

	private double maxError;
	private Train train;
	private boolean started;
	
	public EndMaxErrorStrategy(double maxError) {
		this.maxError = maxError;
		this.started = false;
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldStop() {
		return this.started && this.train.getError()<this.maxError;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(Train train) {
		this.train = train;
		this.started = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postIteration() {
		this.started = true;		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preIteration() {
	
	}

}
