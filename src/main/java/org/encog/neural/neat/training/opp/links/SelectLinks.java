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
package org.encog.neural.neat.training.opp.links;

import java.util.List;
import java.util.Random;

import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATTraining;

/**
 * This interface defines ways that NEAT links can be chosen for mutation.
 */
public interface SelectLinks {

	/**
	 * @return The trainer being used.
	 */
	NEATTraining getTrainer();

	/**
	 * Setup the selector.
	 * @param theTrainer The trainer.
	 */
	void init(NEATTraining theTrainer);

	/**
	 * Select links from the specified genome.
	 * @param rnd A random number generator.
	 * @param genome The genome to select from.
	 * @return A List of link genomes.
	 */
	List<NEATLinkGene> selectLinks(Random rnd, NEATGenome genome);

}
