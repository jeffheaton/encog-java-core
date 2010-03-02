/*
 * Encog(tm) Core v2.4
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

package org.encog.util.benchmark;

import org.encog.mathutil.LinearCongruentialGenerator;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;

/**
 * Class used to generate random training sets.  This will always generate
 * the same number outputs, as it always uses the same seed values.  This
 * allows for the consistent results needed by the benchmark.
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
		
		LinearCongruentialGenerator rand = new LinearCongruentialGenerator(1000);
		
		final NeuralDataSet result = new BasicNeuralDataSet();
		for (int i = 0; i < count; i++) {
			final NeuralData inputData = new BasicNeuralData(inputCount);

			for (int j = 0; j < inputCount; j++) {
				inputData.setData(j, rand.range(min, max));
			}

			final NeuralData idealData = new BasicNeuralData(idealCount);

			for (int j = 0; j < idealCount; j++) {
				idealData.setData(j, rand.range(min, max));
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
