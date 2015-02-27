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
package org.encog.neural.neat;

/**
 * The types of neurons supported by NEAT.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * -----------------------------------------------------------------------------
 * http://www.cs.ucf.edu/~kstanley/
 * Encog's NEAT implementation was drawn from the following three Journal
 * Articles. For more complete BibTeX sources, see NEATNetwork.java.
 * 
 * Evolving Neural Networks Through Augmenting Topologies
 * 
 * Generating Large-Scale Neural Networks Through Discovering Geometric
 * Regularities
 * 
 * Automatic feature selection in neuroevolution
 */
public enum NEATNeuronType {
	/**
	 * Each NEAT network has one bias neuron.
	 */
	Bias,

	/**
	 * Hidden neurons are between the input and output.
	 */
	Hidden,

	/**
	 * Input neurons receive input, they are never altered during evolution.
	 */
	Input,

	/**
	 * Not really a neuron type, as you will never see one of these in the
	 * network. However, it is used to mark an innovation as not affecting a
	 * neuron type, but rather a link.
	 */
	None,

	/**
	 * Output neurons provide output, they are never altered during evolution.
	 */
	Output

}
