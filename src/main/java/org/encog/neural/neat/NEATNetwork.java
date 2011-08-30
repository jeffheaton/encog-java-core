/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.BasicML;
import org.encog.ml.MLContext;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.util.EngineArray;
import org.encog.util.simple.EncogUtility;

/**
 * Implements a NEAT network as a synapse between two layers. In Encog, a NEAT
 * network is created by using a NEATSynapse between an input and output layer.
 * 
 * NEAT networks only have an input and an output layer. There are no actual
 * hidden layers. Rather this synapse will evolve many hidden neurons that have
 * connections that are not easily defined by layers. Connections can be
 * feedforward, recurrent, or self-connected.
 * 
 * NEAT networks relieve the programmer of the need to define the hidden layer
 * structure of the neural network.
 * 
 * The output from the neural network can be calculated normally or using a
 * snapshot. The snapshot mode is slower, but it can be more accurate. The
 * snapshot handles recurrent layers better, as it takes the time to loop
 * through the network multiple times to "flush out" the recurrent links.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATNetwork extends BasicML implements MLRegression, MLError {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 3660295468309926508L;

	public static final String PROPERTY_NETWORK_DEPTH = "depth";
	public static final String PROPERTY_LINKS = "links";
	public static final String PROPERTY_SNAPSHOT = "snapshot";
	
	/**
	 * The activation function.
	 */
	private ActivationFunction activationFunction;
	
	private ActivationFunction outputActivationFunction;

	/**
	 * The depth of the network.
	 */
	private int networkDepth;
	
	private int inputCount;
	private int outputCount;	
	private int activationCycles = 1;	
	private int[] sourceNeuronIndex;
	private int[] targetNeuronIndex;
	private double[] weights;
	private ActivationFunction[] activation;
	private double[] neuronSums;
	private double[] neuronOutput;
	private int inputAndBiasNeuronCount;
	private int cycles;
	private double stabilityThreshold = Double.MAX_VALUE;


    public NEATNetwork(	
    		int[] theSourceNeuronIndex,
    		int[] theTargetNeuronIndex,
    		double[] theWeights,
    		ActivationFunction[] theActivationFunctions,
            int neuronCount,
            int inputNeuronCount,
            int outputNeuronCount,
            int theCycles)
    {
    	// copy weights and source/target indexes
    	this.sourceNeuronIndex = EngineArray.arrayCopy(theSourceNeuronIndex);
    	this.targetNeuronIndex = EngineArray.arrayCopy(theTargetNeuronIndex);
    	this.weights = EngineArray.arrayCopy(theWeights);
    	

    	// copy the activation functions
    	this.activation = new ActivationFunction[theActivationFunctions.length];
    	for(int i=0;i<theActivationFunctions.length;i++) {
    		this.activation[i] = theActivationFunctions[i];
    	}

    	// setup other properties
    	neuronSums = new double[neuronCount];
    	neuronOutput = new double[neuronCount];

    	this.inputCount = inputNeuronCount;
    	this.inputAndBiasNeuronCount = inputNeuronCount+1;
        this.outputCount = outputNeuronCount;
        this.cycles = theCycles;

        // bias is always 1.0
        this.neuronOutput[0] = 1.0;
    }

	/**
	 * Compute the output from this synapse.
	 * 
	 * @param input
	 *            The input to this synapse.
	 * @return The output from this synapse.
	 */
	public MLData compute(final MLData input) {
		double[] work = new double[1];
		
		// reset the state of the neural network
		for(int i=inputAndBiasNeuronCount; i<neuronOutput.length; i++) {
        	neuronSums[i] = 0.0;
        	neuronOutput[i] = 0.0;
        }
				
		// copy the input array in
		EngineArray.arrayCopy(input.getData(), 0, this.neuronOutput, 1, this.inputCount);
		
		// calculate the output for the neural network
		int cyclesLeft = this.cycles;
		boolean stable = false;
		
		// begin the main calculation loop
		while(cyclesLeft>0 && !stable ) {
			
			for (int j = 0; j < weights.length; j++) {
				neuronSums[targetNeuronIndex[j]] += neuronOutput[sourceNeuronIndex[j]]
						* weights[j];
			}

			stable = true;
			
			for (int j = inputAndBiasNeuronCount; j < neuronSums.length; j++) {
				work[0] = neuronSums[j];
				activation[j].activationFunction(work, 0, 1);
				
                if(Math.abs(work[0] - neuronOutput[j]) > stabilityThreshold) {
                    stable = false;
                }
				
				neuronOutput[j] = work[0];
				neuronSums[j] = 0;
			}
			
			cyclesLeft--;
		}
		
		// copy the values from the output neurons
		final MLData result = new BasicMLData(this.outputCount);
		EngineArray.arrayCopy(result.getData(), 0, this.neuronOutput, this.inputCount+1, this.outputCount);
		
		return result;
	}

	/**
	 * @return The activation function.
	 */
	public ActivationFunction getActivationFunction() {
		return this.activationFunction;
	}

	/**
	 * @return The network depth.
	 */
	public int getNetworkDepth() {
		return this.networkDepth;
	}

	/**
	 * Set the activation function.
	 * @param activationFunction The activation function.
	 */
	public void setActivationFunction(
			final ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}

	@Override
	public int getInputCount() {
		return this.inputCount;
	}

	@Override
	public int getOutputCount() {
		return this.outputCount;
	}
	
	@Override
	public void updateProperties() {
		
	}

	public void setInputCount(int i) {
		this.inputCount = i;		
	}
	
	public void setOutputCount(int i) {
		this.outputCount = i;		
	}

	public void setNetworkDepth(int i) {
		this.networkDepth = i;
		
	}

	/**
	 * @return the outputActivationFunction
	 */
	public ActivationFunction getOutputActivationFunction() {
		return outputActivationFunction;
	}

	/**
	 * @param outputActivationFunction the outputActivationFunction to set
	 */
	public void setOutputActivationFunction(
			ActivationFunction outputActivationFunction) {
		this.outputActivationFunction = outputActivationFunction;
	}

	/**
	 * Calculate the error for this neural network. 
	 * 
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	public double calculateError(final MLDataSet data) {
		return EncogUtility.calculateRegressionError(this,data);
	}

	public double getStabilityThreshold() {
		return stabilityThreshold;
	}

	public void setStabilityThreshold(double stabilityThreshold) {
		this.stabilityThreshold = stabilityThreshold;
	}	
}
