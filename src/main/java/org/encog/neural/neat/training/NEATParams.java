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
package org.encog.neural.neat.training;

public class NEATParams {
	/**
	 * The activation mutation rate.
	 */
	public double activationMutationRate = 0.1;

	/**
	 * The likelyhood of adding a link.
	 */
	public double chanceAddLink = 0.07;

	/**
	 * The likelyhood of adding a node.
	 */
	public double chanceAddNode = 0.04;

	/**
	 * THe likelyhood of adding a recurrent link.
	 */
	public double chanceAddRecurrentLink = 0.05;

	/**
	 * The compatibility threshold for a species.
	 */
	public double compatibilityThreshold = 0.26;

	/**
	 * The crossover rate.
	 */
	public double crossoverRate = 0.7;

	/**
	 * The max activation perturbation.
	 */
	public double maxActivationPerturbation = 0.1;

	/**
	 * The maximum number of species.
	 */
	public int maxNumberOfSpecies = 0;

	/**
	 * The maximum number of neurons.
	 */
	public double maxPermittedNeurons = 100;

	/**
	 * The maximum weight perturbation.
	 */
	public double maxWeightPerturbation = 0.5;

	/**
	 * The mutation rate.
	 */
	public double mutationRate = 0.2;

	/**
	 * The number of link add attempts.
	 */
	public int numAddLinkAttempts = 5;

	/**
	 * The number of generations allowed with no improvement.
	 */
	public int numGensAllowedNoImprovement = 15;

	/**
	 * The number of tries to find a looped link.
	 */
	public int numTrysToFindLoopedLink = 5;

	/**
	 * The number of tries to find an old link.
	 */
	public int numTrysToFindOldLink = 5;

	/**
	 * The probability that the weight will be totally replaced.
	 */
	public double probabilityWeightReplaced = 0.1;

}
