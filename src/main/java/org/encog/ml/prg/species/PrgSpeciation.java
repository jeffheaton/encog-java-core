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
package org.encog.ml.prg.species;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.species.ThresholdSpeciation;
import org.encog.ml.prg.EncogProgram;

/**
 * Perform speciation for two Encog programs. This is a threshold based
 * speciation, similar to that used for NEAT. Any genomes with a compatibility
 * score below a specified threshold will be in the same species.
 */
public class PrgSpeciation extends ThresholdSpeciation {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;

	public PrgSpeciation() {
		setCompatibilityThreshold(15);
		setMaxNumberOfSpecies(30);
		setNumGensAllowedNoImprovement(15);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getCompatibilityScore(final Genome genome1,
			final Genome genome2) {
		final CompareEncogProgram comp = new CompareEncogProgram();
		final double d = comp.compare((EncogProgram) genome1,
				(EncogProgram) genome2);
		return d;
	}

}
