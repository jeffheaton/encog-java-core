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
package org.encog.neural.freeform;

import java.util.List;

/**
 * Defines a freeform layer. A layer is a group of similar neurons.
 *
 */
public interface FreeformLayer {
	
	/**
	 * Add a neuron to this layer.
	 * @param basicFreeformNeuron The neuron to add.
	 */
	void add(FreeformNeuron basicFreeformNeuron);
	
	/**
	 * @return The neurons in this layer.
	 */
	List<FreeformNeuron> getNeurons();

	/**
	 * @return True if this layer has bias.
	 */
	boolean hasBias();

	/**
	 * Set the activation for the specified index.
	 * @param i The index.
	 * @param data The data for that index.
	 */
	void setActivation(int i, double data);

	/**
	 * @return The size of this layer, including bias.
	 */
	int size();

	/**
	 * @return The size of this layer, no bias counted.
	 */
	int sizeNonBias();
}
