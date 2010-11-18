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
