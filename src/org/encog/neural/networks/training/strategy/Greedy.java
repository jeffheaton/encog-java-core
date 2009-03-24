package org.encog.neural.networks.training.strategy;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.Network;
import org.encog.neural.networks.NetworkCODEC;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Greedy implements Strategy {

	private Train train;
	private double lastError;
	private Double[] lastNetwork;
	private boolean ready;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void init(Train train) {
		this.train = train;
		this.ready = false;
	}

	@Override
	public void postIteration() {
		if( this.ready )
		{
		if( this.train.getError()>this.lastError )
		{
			this.train.setError(this.lastError);
			NetworkCODEC.arrayToNetwork(this.lastNetwork, train.getNetwork());
		}
		}
		else 
			this.ready = true;
	}

	@Override
	public void preIteration() {
		
		BasicNetwork network = this.train.getNetwork();	
		if( network!=null )
		{
			this.lastError = this.train.getError();
			this.lastNetwork = NetworkCODEC.networkToArray(network);
			this.train.setError(this.lastError);
		}
	}
}
