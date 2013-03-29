/*
 * Encog(tm) Core v3.2 - Java Version
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
package org.encog.ml.prg.species;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.species.ThresholdSpeciation;
import org.encog.ml.prg.EncogProgram;

public class PrgSpeciation extends ThresholdSpeciation {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;


	public PrgSpeciation() {
		this.setCompatibilityThreshold(15);
		this.setMaxNumberOfSpecies(30);
		this.setNumGensAllowedNoImprovement(15);
	}
	
	
	/**
	 * Get the compatibility score with another genome. Used to determine
	 * species.
	 * 
	 * @param genome
	 *            The other genome.
	 * @return The score.
	 */
	@Override
	public double getCompatibilityScore(final Genome genome1,
			final Genome genome2) {
		CompareEncogProgram comp = new CompareEncogProgram();
		double d = comp.compare((EncogProgram)genome1, (EncogProgram)genome2);
		return d;
	}

}
