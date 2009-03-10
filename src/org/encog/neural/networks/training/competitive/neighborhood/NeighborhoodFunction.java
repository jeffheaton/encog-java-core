package org.encog.neural.networks.training.competitive.neighborhood;

public interface NeighborhoodFunction {
	double function(int currentNeuron, int bestNeuron, double d);

}
