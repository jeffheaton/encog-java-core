package org.encog.neural.networks.training.strategy.end;

import org.encog.neural.networks.training.Train;

public class EndIterationsStrategy implements EndTrainingStrategy {

	private int maxIterations;
	private int currentIteration;
	
	public EndIterationsStrategy(int maxIterations) {
		this.maxIterations = maxIterations;
		this.currentIteration = 0;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldStop() {
		return (this.currentIteration>=this.maxIterations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(Train train) {
	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postIteration() {
		this.currentIteration++;
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preIteration() {
	}
}
