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
package org.encog.ml.genetic;

import org.encog.ml.genetic.genome.Genome;
import org.encog.util.concurrency.EngineConcurrency;
import org.encog.util.concurrency.MultiThreadable;
import org.encog.util.concurrency.TaskGroup;

/**
 * Provides a basic implementation of a genetic algorithm.
 */
public class BasicGeneticAlgorithm extends GeneticAlgorithm {

	/**
	 * Is this the first iteration.
	 */
	private boolean first = true;
	
	/**
	 * Modify the weight matrix and bias values based on the last call to
	 * calcError.
	 * 
	 * @throws NeuralNetworkException
	 */
	@Override
	public final void iteration() {

		if (this.first) {
			EngineConcurrency.getInstance().setThreadCount(getThreadCount());
			getPopulation().claim(this);
			this.first = false;
		}

		final int countToMate = (int) (getPopulation().getPopulationSize() 
				* getPercentToMate());
		final int offspringCount = countToMate * 2;
		int offspringIndex = getPopulation().getPopulationSize()
				- offspringCount;
		final int matingPopulationSize = (int) (getPopulation()
				.getPopulationSize() * getMatingPopulation());

		final TaskGroup group = EngineConcurrency.getInstance()
				.createTaskGroup();

		// mate and form the next generation
		for (int i = 0; i < countToMate; i++) {
			final Genome mother = getPopulation().getGenomes().get(i);
			final int fatherInt = (int) (Math.random() * matingPopulationSize);
			final Genome father = getPopulation().getGenomes().get(fatherInt);
			final Genome child1 = getPopulation().getGenomes().get(
					offspringIndex);
			final Genome child2 = getPopulation().getGenomes().get(
					offspringIndex + 1);

			final MateWorker worker = new MateWorker(mother, father, child1,
					child2);

			EngineConcurrency.getInstance().processTask(worker, group);
			
			offspringIndex += 2;
		}

		group.waitForComplete();

		// sort the next generation
		getPopulation().sort();
	}
}
