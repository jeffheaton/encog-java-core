package org.encog.neural.networks.training.strategy;

import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;

public class StopTrainingStrategy implements Strategy {

	
	public static final double DEFAULT_MIN_IMPROVEMENT = 0.0000001;
	public static final int DEFAULT_TOLERATE_CYCLES = 100;
	
	private Train train;
	private boolean shouldStop;
	private boolean ready;
	private double lastError;
	private final double minImprovement;
	private final int toleratedCycles;
	private int badCycles;
	
	public StopTrainingStrategy()
	{
		this(StopTrainingStrategy.DEFAULT_MIN_IMPROVEMENT,
				StopTrainingStrategy.DEFAULT_TOLERATE_CYCLES);
	}
	
	public StopTrainingStrategy(double minImprovement,int toleratedCycles)
	{
		this.minImprovement = minImprovement;
		this.toleratedCycles = toleratedCycles;
		this.badCycles = 0;
	}
	
	@Override
	public void init(Train train) {
		this.train = train;
		this.shouldStop = false;
		this.ready = false;
	}

	@Override
	public void postIteration() {
		
		if( ready )
		{
			if( Math.abs(this.lastError-train.getError())<this.minImprovement )
			{
				this.badCycles++;
				if( this.badCycles>this.toleratedCycles )
				{
					shouldStop = true;
				}
			}
			else
				this.badCycles = 0;
		}
		else 
			ready = true;
		
		this.lastError = train.getError();
		
	}

	@Override
	public void preIteration() {	
	}
	
	public boolean shouldStop()
	{
		return this.shouldStop;
	}

}
