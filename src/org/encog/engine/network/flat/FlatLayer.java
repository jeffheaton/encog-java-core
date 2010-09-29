/*
 * Encog(tm) Core v2.5 - Java Version
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

package org.encog.engine.network.flat;

import org.encog.engine.EncogEngine;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.util.EngineArray;

/**
 * Used to configure a flat layer. Flat layers are not kept by a Flat Network,
 * beyond setup.
 */
public class FlatLayer {

	/**
	 * The activation function.
	 */
	private final ActivationFunction activation;

	/**
	 * The neuron count.
	 */
	private final int count;

	/**
	 * The bias activation, usually 1 for bias or 0 for no bias.
	 */
	private final double biasActivation;

	/**
	 * The layer that feeds this layer's context.
	 */
	private FlatLayer contextFedBy;

	/**
	 * Construct a flat layer.
	 *
	 * @param activation
	 *            The activation function.
	 * @param count
	 *            The neuron count.
	 * @param biasActivation
	 *            The bias activation.
	 * @param params
	 *            The parameters.
	 */
	public FlatLayer(final ActivationFunction activation, final int count,
			final double biasActivation, final double[] params) {
		this.activation = activation;
		this.count = count;
		this.biasActivation = biasActivation;
		this.contextFedBy = null;
	}

	/**
	 * @return the activation
	 */
	public ActivationFunction getActivation() {
		return this.activation;
	}

	/**
	 * @return Get the bias activation.
	 */
	public double getBiasActivation() {
		return this.biasActivation;
	}

	/**
	 * @return The number of neurons our context is fed by.
	 */
	public int getContectCount() {
		if (this.contextFedBy == null) {
			return 0;
		} else {
			return this.contextFedBy.getCount();
		}
	}

	/**
	 * @return The layer that feeds this layer's context.
	 */
	public FlatLayer getContextFedBy() {
		return this.contextFedBy;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * @return The total number of neurons on this layer, includes context, bias
	 *         and regular.
	 */
	public int getTotalCount() {
		if (this.contextFedBy == null) {
			return getCount() + (isBias() ? 1 : 0);
		} else {
			return getCount() + (isBias() ? 1 : 0)
					+ this.contextFedBy.getCount();
		}
	}

	/**
	 * @return the bias
	 */
	public boolean isBias() {
		return Math.abs(this.biasActivation) > EncogEngine.DEFAULT_ZERO_TOLERANCE;
	}

	/**
	 * Set the layer that this layer's context is fed by.
	 *
	 * @param from
	 *            The layer feeding.
	 */
	public void setContextFedBy(final FlatLayer from) {
		this.contextFedBy = from;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[FlatLayer: count=");
		result.append(this.count);
		result.append(",bias=");

		if (isBias()) {
			result.append(this.biasActivation);
		} else {
			result.append("false");
		}
		if (this.contextFedBy != null) {
			result.append(",contextFed=");
			if (this.contextFedBy == this) {
				result.append("itself");
			} else {
				result.append(this.contextFedBy);
			}
		}
		result.append("]");
		return result.toString();
	}
}
