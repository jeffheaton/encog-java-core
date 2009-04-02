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
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
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
	
	public void init(Train train) {
		this.train = train;
		this.shouldStop = false;
		this.ready = false;
	}
	
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

	public void preIteration() {	
	}
	
	public boolean shouldStop()
	{
		return this.shouldStop;
	}

}
