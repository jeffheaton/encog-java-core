/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */
package org.encog.engine.network.flat;

import org.encog.engine.EncogEngine;
import org.encog.engine.util.EngineArray;

/**
 * Used to configure a flat layer. Flat layers are not kept by a Flat Network,
 * beyond setup.
 */
public class FlatLayer {

	/**
	 * The activation function.
	 */
	private final int activation;

	/**
	 * The neuron count.
	 */
	private final int count;

	/**
	 * The bias activation, usually 1 for bias or 0 for no bias.
	 */
	private final double biasActivation;

	/**
	 * The params for the activation function.
	 */
	private final double[] params;

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
	public FlatLayer(final int activation, final int count,
			final double biasActivation, final double[] params) {
		this.activation = activation;
		this.count = count;
		this.biasActivation = biasActivation;
		this.params = EngineArray.arrayCopy(params);
		this.contextFedBy = null;
	}

	/**
	 * @return the activation
	 */
	public int getActivation() {
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
	 * @return The parameters that this activation uses.
	 */
	public double[] getParams() {
		return this.params;
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
	 * @return Convert this layer to a string.
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
