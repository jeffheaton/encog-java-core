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
package org.encog.neural.neat;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.ml.BasicML;
import org.encog.ml.MLError;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.util.EngineArray;
import org.encog.util.simple.EncogUtility;

/**
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
	
    private final NEATLink[] links;
	private final ActivationFunction[] activationFunctions;
    private final double[] preActivation;
    private final double[] postActivation;
    private final int outputIndex;
	private int inputCount;
	private int outputCount;	
	private int activationCycles = 1;
    private boolean hasRelaxed = false;
    private double relaxationThreshold;

    public NEATNetwork(
    		int inputNeuronCount,
            int outputNeuronCount,
            NEATLink[] connectionArray,
            ActivationFunction[] theActivationFunctions)
    {
        links = connectionArray;
        activationFunctions = theActivationFunctions;
        int neuronCount = this.activationFunctions.length;

        preActivation = new double[neuronCount];
        postActivation = new double[neuronCount];

        this.inputCount = inputNeuronCount;
        outputIndex = inputNeuronCount+1;
        this.outputCount = outputNeuronCount;

        // bias
        postActivation[0] = 1.0;
    }

	/**
	 * Compute the output from this synapse.
	 * 
	 * @param input
	 *            The input to this synapse.
	 * @return The output from this synapse.
	 */
	public MLData compute(final MLData input) {
		final MLData result = new BasicMLData(this.outputCount);
		
		// clear from previous
    	EngineArray.fill(this.preActivation, 0.0);
    	EngineArray.fill(this.postActivation, 0.0);
    	postActivation[0] = 1.0;
		
		// copy input
		EngineArray.arrayCopy(input.getData(), 0, this.postActivation, 1, this.inputCount);
				

		// iterate through the network activationCycles times
		for (int i = 0; i < activationCycles; ++i) {
			internalCompute();
		}
		
		// copy output
		EngineArray.arrayCopy(this.postActivation, this.outputIndex, result.getData(), 0, this.outputCount);

		return result;
	}
	
    private void internalCompute()
    {
            for(int j=0; j<links.length; j++) {
                preActivation[links[j].getToNeuron()] += postActivation[links[j].getFromNeuron()] 
                		* links[j].getWeight();
            }

            for(int j=outputIndex; j<preActivation.length; j++)
            {
            	postActivation[j] = preActivation[j];
            	activationFunctions[j].activationFunction(postActivation, j, 1);            	
                preActivation[j] = 0.0F;
            }
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

	public int getActivationCycles() {
		return activationCycles;
	}

	public void setActivationCycles(int activationCycles) {
		this.activationCycles = activationCycles;
	}

	public boolean isHasRelaxed() {
		return hasRelaxed;
	}

	public void setHasRelaxed(boolean hasRelaxed) {
		this.hasRelaxed = hasRelaxed;
	}

	public double getRelaxationThreshold() {
		return relaxationThreshold;
	}

	public void setRelaxationThreshold(double relaxationThreshold) {
		this.relaxationThreshold = relaxationThreshold;
	}

	public NEATLink[] getLinks() {
		return links;
	}

	public double[] getPreActivation() {
		return preActivation;
	}

	public double[] getPostActivation() {
		return postActivation;
	}

	public int getOutputIndex() {
		return outputIndex;
	}	
	
    public ActivationFunction[] getActivationFunctions() {
		return activationFunctions;
	}	

}
