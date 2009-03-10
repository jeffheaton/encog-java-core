package org.encog.neural.networks.training.competitive.neighborhood;

public class NeighborhoodSingle implements NeighborhoodFunction {

	public double function(int currentNeuron, int bestNeuron,double d) {
		if( currentNeuron==bestNeuron )
			return 1.0;
		else 
			return 0.0;
	}

}
