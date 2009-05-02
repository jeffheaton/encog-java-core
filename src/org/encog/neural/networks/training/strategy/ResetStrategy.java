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
 * The reset strategy will reset the weights if the neural network fails to
 * fall below a specified error by a specified number of cycles.  This can
 * be useful to throw out initially "bad/hard" random initializations of the
 * weight matrix.
 * @author jheaton
 *
 */
public class ResetStrategy implements Strategy {
	
	/**
	 * The logging object.
	 */
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
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

	public void init(Train train) {
		this.train = train;		
	}

	public void postIteration() {
		
	}

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
