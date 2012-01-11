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
package org.encog.persist;

/**
 * Some common persistance constants.
 *
 */
public final class PersistConst {
	
	/**
	 * A Hopfield neural network.
	 */
	public static final String TYPE_HOPFIELD = "HopfieldNetwork";
	
	/**
	 * A Boltzmann machine.
	 */
	public static final String TYPE_BOLTZMANN = "BoltzmannMachine";
	
	/**
	 * An ART1 neural network.
	 */
	public static final String TYPE_ART1 = "ART1";
	
	/**
	 * A BAM neural network.
	 */
	public static final String TYPE_BAM = "BAM";
	
	/**
	 * A SOM neural network.
	 */
	public static final String TYPE_SOM = "SOM";
	
	/**
	 * A NEAT neural network.
	 */
	public static final String TYPE_NEAT = "NEATNetwork";
	
	/**
	 * A NEAT population.
	 */
	public static final String TYPE_NEAT_POPULATION = "NEATPopulation";
	
	/**
	 * A species.
	 */
	public static final String TYPE_BASIC_SPECIES = "BasicSpecies";
	
	/**
	 * A neuron gene.
	 */
	public static final String TYPE_NEAT_NEURON_GENE = "NEATNeuronGene";
	
	/**
	 * A support vector machine.
	 */
	public static final String TYPE_SVM = "SVM";
	
	/**
	 * A neural network.
	 */
	public static final String TYPE_BASIC_NETWORK = "BasicNetwork";
	
	/**
	 * A RBF network.
	 */
	public static final String TYPE_RBF_NETWORK = "RBFNetwork";
	
	/**
	 * A name.
	 */
	public static final String NAME = "name";
	
	/**
	 * A description.
	 */
	public static final String DESCRIPTION = "description";
	
	/**
	 * Neurons.
	 */
	public static final String NEURON_COUNT = "neurons";
	
	/**
	 * Thresholds.
	 */
	public static final String THRESHOLDS = "thresholds";
	
	/**
	 * Weights.
	 */
	public static final String WEIGHTS = "weights";
	
	/**
	 * Output.
	 */
	public static final String OUTPUT = "output";

	/**
	 * Native.
	 */
	public static final String NATIVE = "native";
	
	/**
	 * Temperature.
	 */
	public static final String TEMPERATURE = "temperature";
	
	/**
	 * The input count.
	 */
	public static final String INPUT_COUNT = "inputCount";
	
	/**
	 * The output count.
	 */
	public static final String OUTPUT_COUNT = "outputCount";

	/**
	 * List.
	 */
	public static final String LIST = "list";
	
	/**
	 * Data.
	 */
	public static final String DATA = "data";
	
	/**
	 * matrix.
	 */
	public static final String MATRIX = "matrix";
	
	/**
	 * An activation function.
	 */
	public static final String ACTIVATION_TYPE = "af";
	
	/**
	 * The F1 count.
	 */
	public static final String PROPERTY_F1_COUNT = "f1Count";
	
	/**
	 * The F2 count.
	 */
	public static final String PROPERTY_F2_COUNT = "f2Count";
	
	/**
	 * The weights from F1 to F2.
	 */
	public static final String PROPERTY_WEIGHTS_F1_F2 = "weightsF1F2";
	
	/**
	 * The weights from F2 to F1.
	 */
	public static final String PROPERTY_WEIGHTS_F2_F1 = "weightsF2F1";
	
	/**
	 * Activation function.
	 */
	public static final String ACTIVATION_FUNCTION = "activationFunction";
	
	/**
	 * Neuron count.
	 */
	public static final String NEURONS = "neurons";
	
	/**
	 * Type.
	 */
	public static final String TYPE = "type";
	
	/**
	 * Recurrent.
	 */
	public static final String RECURRENT = "recurrent";
	
	/**
	 * Weight.
	 */
	public static final String WEIGHT = "weight";
	
	/**
	 * Links.
	 */
	public static final String LINKS = "links";
	
	/** 
	 * NEAT innovation.
	 */
	public static final String TYPE_NEAT_INNOVATION = "NEATInnovation";
	
	/**
	 * Property id.
	 */
	public static final String PROPERTY_ID = "id";
	
	/**
	 * NEAT genome.
	 */
	public static final String TYPE_NEAT_GENOME = "NEATGenome";
	
	/**
	 * Enabled.
	 */
	public static final String ENABLED = "enabled";
	
	/**
	 * idata.
	 */
	public static final String IDATA = "idata";
	
	/**
	 * Properties.
	 */
	public static final String PROPERTIES = "properties";
	
	/**
	 * Version.
	 */
	public static final String VERSION = "ver";
	
	/**
	 * Depth.
	 */
	public static final String DEPTH = "depth";
	
	/**
	 * Snapshot.
	 */
	public static final String ACTIVATION_CYCLES = "cycles";
	
	/**
	 * Error.
	 */
	public static final String ERROR = "error";
	
	/**
	 * Sigma.
	 */
	public static final String SIGMA = "sigma";
	
	/**
	 * Kernel.
	 */
	public static final String KERNEL = "kernel";
	
	/**
	 * Instar.
	 */
	public static final String INSTAR = "instar";

	/**
	 * Sums
	 */
	public static final String SUMS = "sums";
	
	/**
	 * Private constructor.
	 */
	private PersistConst() {
		
	}

}
