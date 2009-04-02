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

import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.networks.training.LearningRate;
import org.encog.neural.networks.training.Strategy;
import org.encog.neural.networks.training.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartLearningRate implements Strategy {

	private Train train;
	private LearningRate setter;
	private double currentLearningRate;	
	private long trainingSize;
	private double lastError;
	private boolean ready;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void init(Train train) {
		this.train = train;	
		this.ready = false;
		this.setter = (LearningRate)train;
		this.trainingSize = determineTrainingSize();
		this.currentLearningRate = 1.0/this.trainingSize;
		if( logger.isInfoEnabled() )
		{
		logger.info("Starting learning rate: {}", this.currentLearningRate);
		}	
		this.setter.setLearningRate(this.currentLearningRate);
	}
	
	private long determineTrainingSize()
	{
		long result = 0;
		for(@SuppressWarnings("unused")
		final NeuralDataPair pair: this.train.getTraining())
			result++;
		return result;
	}

	public void postIteration() {
		if( this.ready )
		{
			if( this.train.getError()>this.lastError )
			{
				this.currentLearningRate*=0.99;
				this.setter.setLearningRate(this.currentLearningRate);
				if( logger.isInfoEnabled() )
				{
				logger.info("Adjusting learning rate to {}", this.currentLearningRate);
				}
			}
		}
		else
			this.ready = true;
		
	}

	public void preIteration() {
		this.lastError = this.train.getError();		
	}



}
