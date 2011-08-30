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
package org.encog.neural.neat.decode;

import java.util.HashMap;
import java.util.Map;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.genetic.genes.Gene;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

public class DecodeGenome {
	
	public static NEATNetwork decode(NEATGenome genome) {
		
		int connectionCount = genome.getLinks().size();
		int neuronCount = genome.getNeurons().size();
		int inputNeuronCount = genome.getInputCount();
		int outputNeuronCount = genome.getOutputCount();
		
		// construct the arrays
		int[] theSourceNeuronIndex = new int[connectionCount];
		int[] theTargetNeuronIndex = new int[connectionCount];
		double[] theWeights = new double[connectionCount];
		ActivationFunction[] theActivationFunctions = new ActivationFunction[neuronCount];
		
        // put the neurons in the map		
		Map<Integer,Integer> map = new HashMap<Integer,Integer>(neuronCount);
        int i = 0;
        for (final Gene gene : genome.getNeurons().getGenes()) {
			final NEATNeuronGene neuronGene = (NEATNeuronGene) gene;
			map.put((int)neuronGene.getId(), i++);
        }
        
        // build the connections
        i = 0;
        for (final Gene gene : genome.getLinks().getGenes()) {
			final NEATLinkGene linkGene = (NEATLinkGene) gene;
			if (linkGene.isEnabled()) {
				theSourceNeuronIndex[i] = map.get(linkGene.getFromNeuronID());
				theTargetNeuronIndex[i] = map.get(linkGene.getToNeuronID());
				theWeights[i] = linkGene.getWeight();
	            i++;
			}
        }
        
        // setup activation functions
        for(i=0;i<theActivationFunctions.length;i++) {
        	theActivationFunctions[i] = new ActivationSigmoid();
        }
		
		
		// construct and return the network
		return new NEATNetwork(
				theSourceNeuronIndex,
	    		theTargetNeuronIndex,
	    		theWeights,
	    		theActivationFunctions,
	            neuronCount,
	            inputNeuronCount,
	            outputNeuronCount,
	            50);
	}
	
}
