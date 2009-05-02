/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.encog.neural.networks.training.strategy;

import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A hybrid stragey allows a secondary training algorithm to be used.  Once
 * the primary algorithm is no longer improving by much, the secondary will
 * be used.  Using simulated annealing in as a secondary to one of the propagation
 * methods is often a very efficient combination as it can help the propagation
 * method escape a local minimum.  This is particularly true with backpropagation.
 * @author jheaton
 *
 */
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
	
	/**
	 * The logging object.
	 */
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

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

	public void init(Train train) {
		this.mainTrain = train;		
	}

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

	public void preIteration() {
		this.lastError = this.mainTrain.getError();
		
	}
}
