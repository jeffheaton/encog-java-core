package org.encog.neural.networks.training.strategy;

import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResetStrategy implements Strategy {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private double required;
	private int cycles;
	private Train train;
	private int badCycleCount;
	
	public ResetStrategy(double required, int cycles)
	{
		this.required = required;
		this.cycles = cycles;
		this.badCycleCount = 0;
	}

	@Override
	public void init(Train train) {
		this.train = train;		
	}

	@Override
	public void postIteration() {
		
	}

	@Override
	public void preIteration() {
		if( this.train.getError()>this.required )
		{
			this.badCycleCount ++;
			if( badCycleCount>this.cycles )
			{
				if( logger.isDebugEnabled() )
				{
					logger.debug("Failed to imrove network, resetting.");
				}
				this.train.getNetwork().reset();
				this.badCycleCount = 0;
			}
		}
		else
		{
			this.badCycleCount = 0;
		}
	}
}
