/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.training;

import java.util.List;

import org.encog.cloud.EncogCloud;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;

/**
 * Interface for all neural network training methods. This allows the training
 * methods to be largely interchangeable. Though some training methods require
 * specific types of neural network structure.
 */

public interface Train {

	/**
	 * Training strategies can be added to improve the training results. There
	 * are a number to choose from, and several can be used at once.
	 * 
	 * @param strategy
	 *            The strategy to add.
	 */
	void addStrategy(Strategy strategy);

	/**
	 * Should be called once training is complete and no more iterations are
	 * needed. Calling iteration again will simply begin the training again, and
	 * require finishTraining to be called once the new training session is
	 * complete.
	 * 
	 * It is particularly important to call finishTraining for multithreaded
	 * training techniques.
	 */
	void finishTraining();

	/**
	 * Get the current error percent from the training.
	 * 
	 * @return The current error.
	 */
	double getError();

	/**
	 * Get the current best network from the training.
	 * 
	 * @return The best network.
	 */
	BasicNetwork getNetwork();

	/**
	 * @return The strategies to use.
	 */
	List<Strategy> getStrategies();

	/**
	 * @return The training data to use.
	 */
	NeuralDataSet getTraining();

	/**
	 * Perform one iteration of training.
	 */
	void iteration();

	/**
	 * @param error
	 *            Set the current error rate. This is usually used by training
	 *            strategies.
	 */
	void setError(double error);

	/**
	 * Set the cloud use to track the training.
	 * @param cloud The cloud used to track the training.
	 */
	void setCloud(EncogCloud cloud);
	
	/**
	 * @return The cloud used to track the training.
	 */
	EncogCloud getCloud();
	
}
