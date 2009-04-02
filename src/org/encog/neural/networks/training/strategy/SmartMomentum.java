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

import org.encog.neural.networks.training.Momentum;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartMomentum implements Strategy {
	
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
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
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
