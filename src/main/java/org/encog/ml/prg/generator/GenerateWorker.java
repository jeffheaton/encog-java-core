/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.ml.prg.generator;

import java.util.Random;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.train.PrgPopulation;

/**
 * Used to thread the generation process.
 */
public class GenerateWorker implements Runnable {

	/**
	 * The owner.
	 */
	private final AbstractPrgGenerator owner;
	
	/**
	 * A random number generator.
	 */
	private final Random rnd;
	
	/**
	 * The population.
	 */
	private final PrgPopulation population;

	/**
	 * Construct the worker.
	 * @param theOwner The owner.
	 * @param thePopulation The target population.
	 */
	public GenerateWorker(final AbstractPrgGenerator theOwner,
			final PrgPopulation thePopulation) {
		this.owner = theOwner;
		this.population = thePopulation;
		this.rnd = this.owner.getRandomFactory().factor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		final EncogProgram prg = this.owner.attemptCreateGenome(this.rnd,
				this.population);
		this.owner.addPopulationMember(this.population, prg);
	}

}
