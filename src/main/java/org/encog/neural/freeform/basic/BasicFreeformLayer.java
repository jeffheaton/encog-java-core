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
package org.encog.neural.freeform.basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.neural.freeform.FreeformLayer;
import org.encog.neural.freeform.FreeformNeuron;

/**
 * Implements a basic freeform layer.
 *
 */
public class BasicFreeformLayer implements FreeformLayer, Serializable {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The neurons in this layer.
	 */
	private final List<FreeformNeuron> neurons = new ArrayList<FreeformNeuron>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final FreeformNeuron basicFreeformNeuron) {
		this.neurons.add(basicFreeformNeuron);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FreeformNeuron> getNeurons() {
		return this.neurons;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasBias() {
		for (final FreeformNeuron neuron : this.neurons) {
			if (neuron.isBias()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setActivation(final int i, final double activation) {
		this.neurons.get(i).setActivation(activation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.neurons.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int sizeNonBias() {
		int result = 0;
		for (final FreeformNeuron neuron : this.neurons) {
			if (!neuron.isBias()) {
				result++;
			}
		}
		return result;
	}

}
