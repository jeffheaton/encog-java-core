/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.neural.networks.synapse;

import org.encog.neural.networks.layers.Layer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class that implements basic functionality that may be needed by
 * the other synapse classes. Specifically this class handles processing the
 * from and to layer, as well as providing a name and description for the
 * EncogPersistedObject.
 * 
 * @author jheaton
 * 
 */
public abstract class BasicSynapse implements Synapse {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = -2415267745512044497L;

	/**
	 * The from layer.
	 */
	private Layer fromLayer;

	/**
	 * The to layer.
	 */
	private Layer toLayer;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private transient final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @return A clone of this object.
	 */
	@Override
	public abstract Object clone();

	/**
	 * The EncogPersistedObject requires a name and description, however, these
	 * are not used on synapses.
	 * 
	 * @return Not used.
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * @return The from layer.
	 */
	public Layer getFromLayer() {
		return this.fromLayer;
	}

	/**
	 * @return The neuron count from the "from layer".
	 */
	public int getFromNeuronCount() {
		return this.fromLayer.getNeuronCount();
	}

	/**
	 * The EncogPersistedObject requires a name and description, however, these
	 * are not used on synapses.
	 * 
	 * @return Not used.
	 */
	public String getName() {
		return null;
	}

	/**
	 * @return The "to layer".
	 */
	public Layer getToLayer() {
		return this.toLayer;
	}

	/**
	 * @return The neuron count from the "to layer".
	 */
	public int getToNeuronCount() {
		return this.toLayer.getNeuronCount();
	}

	/**
	 * @return True if this is a self-connected synapse. That is, the from and
	 *         to layers are the same.
	 */
	public boolean isSelfConnected() {
		return this.fromLayer == this.toLayer;
	}

	/**
	 * The EncogPersistedObject requires a name and description, however, these
	 * are not used on synapses.
	 * 
	 * @param d
	 *            Not used.
	 */
	public void setDescription(final String d) {

	}

	/**
	 * Set the from layer for this synapse.
	 * 
	 * @param fromLayer
	 *            The from layer for this synapse.
	 */
	public void setFromLayer(final Layer fromLayer) {
		this.fromLayer = fromLayer;
	}

	/**
	 * The EncogPersistedObject requires a name and description, however, these
	 * are not used on synapses.
	 * 
	 * @param n
	 *            Not used.
	 */
	public void setName(final String n) {

	}

	/**
	 * Set the target layer from this synapse.
	 * 
	 * @param toLayer
	 *            The target layer from this synapse.
	 */
	public void setToLayer(final Layer toLayer) {
		this.toLayer = toLayer;
	}

	/**
	 * @return The synapse as a string.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append(": from=");
		result.append(getFromNeuronCount());
		result.append(",to=");
		result.append(getToNeuronCount());
		result.append("]");
		return result.toString();
	}

}
