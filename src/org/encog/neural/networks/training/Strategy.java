package org.encog.neural.networks.training;

public interface Strategy {
	public void init(Train train);
	public void preIteration();
	public void postIteration();
	
}
