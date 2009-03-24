package org.encog.neural.networks.training.strategy;

import org.encog.neural.networks.training.Momentum;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;

public class SmartMomentum implements Strategy {

	private Train train;
	private Momentum setter;
	
	public void init(Train train) {
		this.train = train;
		this.setter = (Momentum)train;
		
	}

	public void postIteration() {
		// TODO Auto-generated method stub
		
	}

	public void preIteration() {
		// TODO Auto-generated method stub
		
	}

}
