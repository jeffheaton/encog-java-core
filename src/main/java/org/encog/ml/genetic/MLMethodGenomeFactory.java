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
package org.encog.ml.genetic;

import org.encog.ml.MLEncodable;
import org.encog.ml.MethodFactory;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.genome.GenomeFactory;
import org.encog.ml.ea.population.Population;

/**
 * A factory to create MLMethod based genomes.
 */
public class MLMethodGenomeFactory implements GenomeFactory {

	/**
	 * The MLMethod factory.
	 */
	private final MethodFactory factory;
	
	/**
	 * The population.
	 */
	private final Population population;

	/**
	 * Construct the genome factory.
	 * @param theFactory The factory.
	 * @param thePopulation The population.
	 */
	public MLMethodGenomeFactory(final MethodFactory theFactory,
			final Population thePopulation) {
		this.factory = theFactory;
		this.population = thePopulation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Genome factor() {
		final Genome result = new MLMethodGenome(
				(MLEncodable) this.factory.factor());
		result.setPopulation(this.population);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Genome factor(final Genome other) {
		final MLMethodGenome result = (MLMethodGenome) factor();
		result.copy(other);
		result.setPopulation(this.population);
		return result;
	}
}
