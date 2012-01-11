/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util.benchmark;

import org.encog.mathutil.LinearCongruentialGenerator;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;

/**
 * Class used to generate random training sets.  This will always generate
 * the same number outputs, as it always uses the same seed values.  This
 * allows for the consistent results needed by the benchmark.
 */
public final class RandomTrainingFactory {

	/**
	 * Generate a random training set.
	 * 
	 * @param seed
	 *            The seed value to use, the same seed value will always produce
	 *            the same results.
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
	public static BasicMLDataSet generate(final long seed, 
			final int count, final int inputCount,
			final int idealCount, final double min, final double max) {
		
		LinearCongruentialGenerator rand = 
			new LinearCongruentialGenerator(seed);
		
		final BasicMLDataSet result = new BasicMLDataSet();
		for (int i = 0; i < count; i++) {
			final MLData inputData = new BasicMLData(inputCount);

			for (int j = 0; j < inputCount; j++) {
				inputData.setData(j, rand.range(min, max));
			}

			final MLData idealData = new BasicMLData(idealCount);

			for (int j = 0; j < idealCount; j++) {
				idealData.setData(j, rand.range(min, max));
			}

			final BasicMLDataPair pair = new BasicMLDataPair(inputData,
					idealData);
			result.add(pair);

		}
		return result;
	}
	
	/**
	 * Generate random training into a training set.
	 * @param training The training set to generate into.
	 * @param seed The seed to use.
	 * @param count How much data to generate.
	 * @param min The low random value.
	 * @param max The high random value.
	 */
	public static void generate(final MLDataSet training, 
			final long seed,
			final int count, 
			final double min, final double max) {
		
		LinearCongruentialGenerator rand 
			= new LinearCongruentialGenerator(seed);
		
		int inputCount = training.getInputSize();
		int idealCount = training.getIdealSize();
		
		for (int i = 0; i < count; i++) {
			final MLData inputData = new BasicMLData(inputCount);

			for (int j = 0; j < inputCount; j++) {
				inputData.setData(j, rand.range(min, max));
			}

			final MLData idealData = new BasicMLData(idealCount);

			for (int j = 0; j < idealCount; j++) {
				idealData.setData(j, rand.range(min, max));
			}

			final BasicMLDataPair pair = new BasicMLDataPair(inputData,
					idealData);
			training.add(pair);

		}
	}


	/**
	 * Private constructor.
	 */
	private RandomTrainingFactory() {

	}
}
