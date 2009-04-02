package org.encog.neural.networks.training.strategy;

import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HybridStrategy implements Strategy {
	
	public static final double DEFAULT_MIN_IMPROVEMENT = 0.00001;
	public static final int DEFAULT_TOLERATE_CYCLES = 10;
	
	private Train mainTrain;
	private Train altTrain;
	private double lastImprovement;
	private double lastError;
	private boolean ready;
	private int lastHybrid;
	private double minImprovement;
	private int tolerateMinImprovement;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	public HybridStrategy(Train altTrain)
	{
		this(altTrain,
				HybridStrategy.DEFAULT_MIN_IMPROVEMENT,
				HybridStrategy.DEFAULT_TOLERATE_CYCLES);
	}
	
	public HybridStrategy(Train altTrain,double minImprovement,int tolerateMinImprovement)
	{
		this.altTrain = altTrain;
		this.ready = false;
		this.lastHybrid = 0;
		this.minImprovement = minImprovement;
		this.tolerateMinImprovement = tolerateMinImprovement;
	}

	@Override
	public void init(Train train) {
		this.mainTrain = train;		
	}

	@Override
	public void postIteration() {
		if(ready )
		{
			double currentError = this.mainTrain.getError();
			this.lastImprovement = (currentError-lastError)/lastError;
			if( logger.isTraceEnabled() )
			{
				logger.trace("Last improvement: {}", this.lastImprovement );
			}
			
			if( (this.lastImprovement>0) ||
				(Math.abs(this.lastImprovement)<this.minImprovement) )
			{
				this.lastHybrid++;
				
				if( this.lastHybrid > this.tolerateMinImprovement )
				{
					this.lastHybrid = 0;

					if( logger.isDebugEnabled() )
					{
						logger.debug("Performing hybrid cycle" );
					}
					
					for(int i=0;i<5;i++)
					{
						this.altTrain.iteration();
					}
				}
			}
		}
		else
		{
			ready = true;
		}
	}

	@Override
	public void preIteration() {
		this.lastError = this.mainTrain.getError();
		
	}
}
