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

/**
 * Training strategies can be added to training algorithms.  Training 
 * strategies allow different additional logic to be added to an existing
 * training algorithm.  There are a number of different training strategies
 * that can perform various tasks, such as adjusting the learning rate or 
 * momentum, or terminating training when improvement diminishes.  Other 
 * strategies are provided as well.
 * 
 * @author jheaton
 *
 */
public interface Strategy {
	
	/**
	 * Initialize this strategy.
	 * @param train The training algorithm.
	 */
	void init(Train train);
	
	/**
	 * Called just before a training iteration.
	 */
	void preIteration();
	
	/**
	 * Called just after a training iteration.
	 */
	void postIteration();
	
}
