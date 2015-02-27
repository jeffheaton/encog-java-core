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
package org.encog.neural.hyperneat;

import java.util.List;
import java.util.Random;

import org.encog.engine.network.activation.ActivationBipolarSteepenedSigmoid;
import org.encog.engine.network.activation.ActivationClippedLinear;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationGaussian;
import org.encog.engine.network.activation.ActivationSIN;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;
import org.encog.util.obj.ChooseObject;

/**
 * A HyperNEAT genome.
 */
public class HyperNEATGenome extends NEATGenome {

	/**
	 * A HyperNEAT genome.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Build the CPPN activation functions.
	 * @param activationFunctions The activation functions collection to add to.
	 */
	public static void buildCPPNActivationFunctions(
			final ChooseObject<ActivationFunction> activationFunctions) {
		activationFunctions.add(0.25, new ActivationClippedLinear());
		activationFunctions.add(0.25, new ActivationBipolarSteepenedSigmoid());
		activationFunctions.add(0.25, new ActivationGaussian());
		activationFunctions.add(0.25, new ActivationSIN());
		activationFunctions.finalizeStructure();
	}

	/**
	 * Construct a HyperNEAT genome.
	 */
	public HyperNEATGenome() {

	}

	public HyperNEATGenome(final HyperNEATGenome other) {
		super(other);
	}

	/**
	 * Construct a HyperNEAT genome from a list of neurons and links.
	 * @param neurons The neurons.
	 * @param links The links.
	 * @param inputCount The input count.
	 * @param outputCount The output count.
	 */
	public HyperNEATGenome(final List<NEATNeuronGene> neurons,
			final List<NEATLinkGene> links, final int inputCount,
			final int outputCount) {
		super(neurons, links, inputCount, outputCount);
	}

	/**
	 * Construct a random HyperNEAT genome.
	 * @param rnd Random number generator.
	 * @param pop The target population.
	 * @param inputCount The input count.
	 * @param outputCount The output count.
	 * @param connectionDensity The connection densitoy, 1.0 for fully connected.
	 */
	public HyperNEATGenome(final Random rnd, final NEATPopulation pop,
			final int inputCount, final int outputCount,
			final double connectionDensity) {
		super(rnd, pop, inputCount, outputCount, connectionDensity);

	}
}
