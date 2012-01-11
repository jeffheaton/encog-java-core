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
package org.encog.neural.flat;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationFunction;

/**
 * Used to configure a flat layer. Flat layers are not kept by a Flat Network,
 * beyond setup.
 */
public class FlatLayer {

	/**
	 * The activation function.
	 */
	private ActivationFunction activation;

	/**
	 * The neuron count.
	 */
	private final int count;

	/**
	 * The bias activation, usually 1 for bias or 0 for no bias.
	 */
	private double biasActivation;

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
	 */
	public FlatLayer(final ActivationFunction activation, final int count,
			final double biasActivation) {
		this.activation = activation;
		this.count = count;
		this.biasActivation = biasActivation;
		this.contextFedBy = null;
	}

	/**
	 * @return the activation
	 */
	public final ActivationFunction getActivation() {
		return this.activation;
	}

	/**
	 * @return Get the bias activation.
	 */
	public final double getBiasActivation() {
		if (hasBias()) {
			return this.biasActivation;
		} else {
			return 0;
		}
	}

	/**
	 * @return The number of neurons our context is fed by.
	 */
	public final int getContextCount() {
		if (this.contextFedBy == null) {
			return 0;
		} else {
			return this.contextFedBy.getCount();
		}
	}

	/**
	 * @return The layer that feeds this layer's context.
	 */
	public final FlatLayer getContextFedBy() {
		return this.contextFedBy;
	}

	/**
	 * @return the count
	 */
	public final int getCount() {
		return this.count;
	}

	/**
	 * @return The total number of neurons on this layer, includes context, bias
	 *         and regular.
	 */
	public final int getTotalCount() {
		if (this.contextFedBy == null) {
			return getCount() + (hasBias() ? 1 : 0);
		} else {
			return getCount() + (hasBias() ? 1 : 0)
					+ this.contextFedBy.getCount();
		}
	}

	/**
	 * @return the bias
	 */
	public final boolean hasBias() {
		return Math.abs(this.biasActivation) > Encog.DEFAULT_DOUBLE_EQUAL;
	}

	/**
	 * @param activation
	 *            the activation to set
	 */
	public final void setActivation(final ActivationFunction activation) {
		this.activation = activation;
	}

	/**
	 * Set the bias activation.
	 * 
	 * @param a
	 *            The bias activation.
	 */
	public void setBiasActivation(final double a) {
		this.biasActivation = a;
	}

	/**
	 * Set the layer that this layer's context is fed by.
	 * 
	 * @param from
	 *            The layer feeding.
	 */
	public final void setContextFedBy(final FlatLayer from) {
		this.contextFedBy = from;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append(": count=");
		result.append(this.count);
		result.append(",bias=");

		if (hasBias()) {
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
