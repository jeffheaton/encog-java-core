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

package org.encog.neural.networks.training;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.data.NeuralDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class that implements basic training for most training algorithms.
 * Specifically training strategies can be added to enhance the training.
 * @author jheaton
 *
 */
public abstract class BasicTraining implements Train {

	private List<Strategy> strategies = new ArrayList<Strategy>();
	/**
	 * The training data.
	 */
	private NeuralDataSet training;
	
	private double error;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public void addStrategy(Strategy strategy) {
		strategy.init(this);
		this.strategies.add(strategy);		
	}
	
	
	
	public List<Strategy> getStrategies() {
		return strategies;
	}

	public NeuralDataSet getTraining() {
		return training;
	}



	public void setTraining(NeuralDataSet training) {
		this.training = training;
	}



	public void preIteration()
	{
		for( Strategy strategy: this.strategies )
		{
			strategy.preIteration();
		}
	}
	
	public void postIteration()
	{
		for( Strategy strategy: this.strategies )
		{
			strategy.postIteration();
		}
	}



	public double getError() {
		return error;
	}



	public void setError(double error) {
		this.error = error;
	}
	
	
	
	
	

}
