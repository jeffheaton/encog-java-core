/*
  * Encog Neural Network and Bot Library for Java
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * Copyright 2008, Heaton Research Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
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
package org.encog.neural.feedforward.train;

import org.encog.neural.feedforward.FeedforwardNetwork;


/**
 * Train: Interface for all feedforward neural network training
 * methods.  There are currently three training methods define:
 * 
 * Backpropagation
 * Genetic Algorithms
 * Simulated Annealing
 *  
 * @author Jeff Heaton
 * @version 2.1
 */

public interface Train {

	/**
	 * Get the current error percent from the training.
	 * @return The current error.
	 */
	public double getError();

	/**
	 * Get the current best network from the training.
	 * @return The best network.
	 */
	public FeedforwardNetwork getNetwork();

	/**
	 * Perform one iteration of training.
	 */
	public void iteration();
}
