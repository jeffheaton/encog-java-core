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
package org.encog.util.benchmark;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.util.randomize.RangeRandomizer;

/**
 * Class used to generate random training sets.
 * 
 * @author jheaton
 * 
 */
public final class RandomTrainingFactory {

	/**
	 * Generate a random training set.
	 * 
	 * @param count
	 *            How many training items to generate.
	 * @param inputCount
	 *            How many input numbers.
	 * @param idealCount
	 *            How many ideal numbers.
	 * @param min
	 *            The minimum random number.
	 * @param max
	 *            The maximum random number.
	 * @return The random training set.
	 */
	public static NeuralDataSet generate(final int count, final int inputCount,
			final int idealCount, final double min, final double max) {
		final NeuralDataSet result = new BasicNeuralDataSet();
		for (int i = 0; i < count; i++) {
			final NeuralData inputData = new BasicNeuralData(inputCount);

			for (int j = 0; j < inputCount; j++) {
				inputData.setData(j, RangeRandomizer.randomize(min, max));
			}

			final NeuralData idealData = new BasicNeuralData(inputCount);

			for (int j = 0; j < idealCount; j++) {
				idealData.setData(j, RangeRandomizer.randomize(min, max));
			}

			final BasicNeuralDataPair pair = new BasicNeuralDataPair(inputData,
					idealData);
			result.add(pair);

		}
		return result;
	}

	/**
	 * Private constructor.
	 */
	private RandomTrainingFactory() {

	}
}
