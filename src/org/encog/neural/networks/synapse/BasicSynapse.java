/*
 * Encog(tm) Core v2.4
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

package org.encog.neural.networks.synapse;

import org.encog.neural.networks.layers.Layer;
import org.encog.persist.BasicPersistedSubObject;
import org.encog.persist.EncogCollection;

/**
 * An abstract class that implements basic functionality that may be needed by
 * the other synapse classes. Specifically this class handles processing the
 * from and to layer, as well as providing a name and description for the
 * EncogPersistedObject.
 *
 * @author jheaton
 *
 */
public abstract class BasicSynapse extends BasicPersistedSubObject 
	implements Synapse  {

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
	 * @return A clone of this object.
	 */
	@Override
	public abstract Object clone();

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
	 * {@inheritDoc}
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
