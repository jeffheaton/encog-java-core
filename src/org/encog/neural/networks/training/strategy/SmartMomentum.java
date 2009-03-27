package org.encog.neural.networks.training.strategy;

import org.encog.neural.networks.training.Momentum;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartMomentum implements Strategy {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	public static final double MIN_IMPROVEMENT = 0.0001;
	public static final double MAX_MOMENTUM = 4;
	public static final double START_MOMENTUM = 0.1;
	public static final double MOMENTUM_INCREASE = 0.01;
	public static final double MOMENTUM_CYCLES = 10;
	
	private Train train;
	private Momentum setter;
	private double lastImprovement;
	private double lastError;
	private boolean ready;
	private int lastMomentum;
	private double currentMomentum;
	
	public void init(Train train) {
		this.train = train;
		this.setter = (Momentum)train;
		ready = false;
		this.setter.setMomentum(0.0);
		this.currentMomentum = 0;
		
	}

	public void preIteration() {
		this.lastError = this.train.getError();
	}
	
	public void postIteration() {
		if(ready )
		{
			double currentError = this.train.getError();
			this.lastImprovement = (currentError-lastError)/lastError;
			if( logger.isTraceEnabled() )
			{
				logger.trace("Last improvement: {}", this.lastImprovement );
			}
			
			if( (this.lastImprovement>0) ||
				(Math.abs(this.lastImprovement)<SmartMomentum.MIN_IMPROVEMENT) )
			{
				this.lastMomentum++;
				
				if( this.lastMomentum > SmartMomentum.MOMENTUM_CYCLES )
				{
					this.lastMomentum = 0;
					if( ((int)this.currentMomentum)==0 )
					{
						this.currentMomentum = SmartMomentum.START_MOMENTUM;
					}
					this.currentMomentum*=(1.0+SmartMomentum.MOMENTUM_INCREASE);
					this.setter.setMomentum(this.currentMomentum);
					if( logger.isDebugEnabled() )
					{
						logger.trace("Adjusting momentum: {}", this.currentMomentum );
					}
				}
			}
			else
			{
				if( logger.isDebugEnabled() )
				{
					logger.trace("Setting momentum back to zero." );
				}

				this.currentMomentum = 0;
				this.setter.setMomentum(0);
			}
		}
		else
		{
			ready = true;
		}
	}

}
