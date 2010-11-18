/*
 * Encog(tm) Core v2.6 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.neural.networks.synapse;

/**
 * Specifies the type of synapse to be created.
 * @author jheaton
 *
 */
public enum SynapseType {
	/**
	 * OneToOne - Each neuron is connected to the same neuron number
	 * in the next layer.  The two layers must have the same number
	 * of neurons.
	 */
	OneToOne,
	
	/**
	 * Weighted - The neurons are connected between the two levels
	 * with weights.  These weights change during training.
	 */
	Weighted,
	
	/**
	 * Weightless - Every neuron is connected to every other neuron
	 * in the next layer, but there are no weights.
	 */
	Weightless,
	
	/**
	 * Direct - Input is simply passed directly to the next layer.
	 */
	Direct,
	
	/**
	 * NEAT - A synapse that contains a NEAT network.
	 */
	NEAT,
	
	/**
	 * Normalize - A synapse that normalizes the data.  Used to implement
	 * a SOM.
	 */
	Normalize
}
