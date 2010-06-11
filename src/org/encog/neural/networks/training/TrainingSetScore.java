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

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;

/**
 * Calculate a score based on a training set. This class allows simulated
 * annealing or genetic algorithms just as you would any other training set
 * based training method.
 */
public class TrainingSetScore implements CalculateScore {

	/**
	 * The training set.
	 */
	private final NeuralDataSet training;

	/**
	 * Construct a training set score calculation.
	 * 
	 * @param training
	 *            The training data to use.
	 */
	public TrainingSetScore(final NeuralDataSet training) {
		this.training = training;
	}

	/**
	 * Calculate the score for the network.
	 * @param network The network to calculate for.
	 * @return The score.
	 */
	public double calculateScore(final BasicNetwork network) {
		return network.calculateError(this.training);
	}

	/**
	 * A training set based score should always seek to lower the error,
	 * as a result, this method always returns true.
	 * @return Returns true.
	 */
	public boolean shouldMinimize() {
		return true;
	}

}
