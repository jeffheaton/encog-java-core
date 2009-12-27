/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
package org.encog.neural.networks.layers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.synapse.DirectSynapse;
import org.encog.neural.networks.synapse.OneToOneSynapse;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.neural.networks.synapse.WeightedSynapse;
import org.encog.neural.networks.synapse.WeightlessSynapse;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.BasicLayerPersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic functionality that most of the neural layers require. The basic layer
 * is often used by itself to implement forward or recurrent layers. Other layer
 * types are based on the basic layer as well.
 * 
 * The layer will either have thresholds are not. Thresholds are values that
 * correspond to each of the neurons. The threshold values will be added to the
 * output calculated for each neuron. Together with the weight matrix the
 * threshold values make up the memory of the neural network. When the neural
 * network is trained, these threshold values (along with the weight matrix
 * values) will be modified.
 * 
 * @author jheaton
 */
public class BasicLayer implements Layer, Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -5682296868750703898L;

	/**
	 * The logging object.
	 */
	private static final transient Logger LOGGER = LoggerFactory
			.getLogger(BasicLayer.class);

	/**
	 * The outbound synapse connections from this layer.
	 */
	private final List<Synapse> next = new ArrayList<Synapse>();

	/**
	 * The x-coordinate of this layer, used for GUI rendering.
	 */
	private int x;

	/**
	 * The y-coordinate of this layer, used for GUI rendering.
	 */
	private int y;

	/**
	 * The id of this level.
	 */
	private int id;

	/**
	 * Which activation function to use for this layer.
	 */
	private ActivationFunction activationFunction;

	/**
	 * The description for this object.
	 */
	private String description;

	/**
	 * The name for this object.
	 */
	private String name;

	/**
	 * How many neurons does this layer hold.
	 */
	private int neuronCount;

	/**
	 * The threshold values for this layer.
	 */
	private double[] threshold;

	/**
	 * The network that this layer belongs to.
	 */
	private BasicNetwork network;

	/**
	 * Default constructor, mainly so the workbench can easily create a default
	 * layer.
	 */
	public BasicLayer() {
		this(1);
	}

	/**
	 * Construct this layer with a non-default threshold function.
	 * 
	 * @param activationFunction
	 *            The threshold function to use.
	 * @param neuronCount
	 *            How many neurons in this layer.
	 * @param hasThreshold
	 *            True if this layer has threshold values.
	 */
	public BasicLayer(final ActivationFunction activationFunction,
			final boolean hasThreshold, final int neuronCount) {
		this.neuronCount = neuronCount;
		this.id = -1;
		setActivationFunction(activationFunction);
		if (hasThreshold) {
			this.threshold = new double[neuronCount];
		}
	}

	/**
	 * Construct this layer with a sigmoid threshold function.
	 * 
	 * @param neuronCount
	 *            How many neurons in this layer.
	 */
	public BasicLayer(final int neuronCount) {
		this(new ActivationTANH(), true, neuronCount);
	}

	/**
	 * Add a layer as the next layer. The layer will be added with a weighted
	 * synapse.
	 * 
	 * @param next
	 *            THe next layer.
	 */
	public void addNext(final Layer next) {
		addNext(next, SynapseType.Weighted);
	}

	/**
	 * @param next
	 *            The next layer to add.
	 * @param type
	 *            The synapse type to use for this layer.
	 */
	public void addNext(final Layer next, final SynapseType type) {
		Synapse synapse = null;

		if (this.network == null) {
			throw new NeuralNetworkError(
		"Can't add to this layer, it is not yet part of a network itself.");
		}

		next.setNetwork(this.network);
		this.network.getStructure().assignID(next);

		switch (type) {
		case OneToOne:
			synapse = new OneToOneSynapse(this, next);
			break;
		case Weighted:
			synapse = new WeightedSynapse(this, next);
			break;
		case Weightless:
			synapse = new WeightlessSynapse(this, next);
			break;
		case Direct:
			synapse = new DirectSynapse(this, next);
			break;
		default:
			throw new NeuralNetworkError("Unknown synapse type");
		}

		if (synapse == null) {
			final String str = "Unknown synapse type.";
			if (BasicLayer.LOGGER.isErrorEnabled()) {
				BasicLayer.LOGGER.error(str);
			}
			throw new NeuralNetworkError(str);
		} else {
			this.next.add(synapse);
		}
	}

	/**
	 * Add a synapse to the list of outbound synapses. Usually you should simply
	 * call the addLayer method to add to the outbound list.
	 * 
	 * @param synapse
	 *            The synapse to add.
	 */
	public void addSynapse(final Synapse synapse) {
		this.next.add(synapse);
	}

	/**
	 * Compare this layer to another.
	 * @return    The value 0 if the argument layer is equal to this layer; a
	 *            value less than 0 if this layer is less
	 *            than the argument; and a value greater than 0 if this
	 *            layer is greater than the layer argument.
	 * @param other The other layer to compare.
	 */
	public int compareTo(final Layer other) {
		if (other.getID() == getID()) {
			return 0;
		} else if (other.getID() > getID()) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 * Compute the outputs for this layer given the input pattern. The output is
	 * also stored in the fire instance variable.
	 * 
	 * @param pattern
	 *            The input pattern.
	 * @return The output from this layer.
	 */
	public NeuralData compute(final NeuralData pattern) {

		final NeuralData result = pattern.clone();

		if (hasThreshold()) {
			// apply the thresholds
			for (int i = 0; i < this.threshold.length; i++) {
				result.setData(i, result.getData(i) + this.threshold[i]);
			}
		}

		// apply the activation function
		getActivationFunction().activationFunction(result.getData());

		return result;
	}

	/**
	 * Create a persistor for this layer.
	 * 
	 * @return The new persistor.
	 */
	public Persistor createPersistor() {
		return new BasicLayerPersistor();
	}

	/**
	 * @return The activation function for this layer.
	 */
	public ActivationFunction getActivationFunction() {
		return this.activationFunction;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return The id of this layer.
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The network that owns this layer.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Get the neuron count for this layer.
	 * 
	 * @return the neuronCount
	 */
	public int getNeuronCount() {
		return this.neuronCount;
	}

	/**
	 * @return The outbound synapse connections.
	 */
	public List<Synapse> getNext() {
		return this.next;
	}

	/**
	 * @return The list of layers that the outbound synapses connect to.
	 */
	public Collection<Layer> getNextLayers() {
		final Collection<Layer> result = new HashSet<Layer>();
		for (final Synapse synapse : this.next) {
			result.add(synapse.getToLayer());
		}
		return result;
	}

	/**
	 * @return The threshold values.
	 */
	public double[] getThreshold() {
		return this.threshold;
	}

	/**
	 * Get an individual threshold value.
	 * 
	 * @param index
	 *            The threshold value to get.
	 * @return The threshold value.
	 */
	public double getThreshold(final int index) {
		if (!hasThreshold()) {
			final String str = 
				"Attempting to access threshold on a thresholdless layer.";
			if (BasicLayer.LOGGER.isErrorEnabled()) {
				BasicLayer.LOGGER.error(str);
			}
			throw new NeuralNetworkError(str);
		}
		return this.threshold[index];
	}

	/**
	 * @return The x-coordinate. Used when the layer is displayed in a GUI.
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * @return The y-coordinate. Used when the layer is displayed in a GUI.
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * @return True if threshold values are present.
	 */
	public boolean hasThreshold() {
		return this.threshold != null;
	}

	/**
	 * Determine if this layer is connected to another layer.
	 * 
	 * @param layer
	 *            A layer to check and see if this layer is connected to.
	 * @return True if the two layers are connected.
	 */
	public boolean isConnectedTo(final Layer layer) {
		for (final Synapse synapse : this.next) {
			if (synapse.getToLayer() == layer) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return True if this layer is connected to intself.
	 */
	public boolean isSelfConnected() {
		for (final Synapse synapse : this.next) {
			if (synapse.isSelfConnected()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Process the input pattern. For the basic layer, nothing is done. This is
	 * how the context layer gets a chance to record the input. Other similar
	 * functions, where access is needed to the input.
	 * 
	 * @param pattern
	 *            The input to this layer.
	 */
	public void process(final NeuralData pattern) {
	}

	/**
	 * Get the output from this layer when called in a recurrent manor. For the
	 * BaiscLayer, this is not implemented.
	 * 
	 * @return The output when called in a recurrent way.
	 */
	public NeuralData recur() {
		return null;
	}

	/**
	 * Set the activation function for this layer.
	 * 
	 * @param f
	 *            The activation function.
	 */
	public void setActivationFunction(final ActivationFunction f) {
		this.activationFunction = f;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Set the id for this layer.
	 * @param id The id for this layer.
	 */
	public void setID(final int id) {
		this.id = id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Set the network for this layer.
	 * @param network The network for this layer.
	 */
	public void setNetwork(final BasicNetwork network) {
		this.network = network;
	}

	/**
	 * Set the neuron count. This just sets it, it does not make any adjustments
	 * to the class. To automatically change the neuron count refer to the
	 * pruning classes.
	 * 
	 * @param neuronCount
	 *            The new neuron count.
	 */
	public void setNeuronCount(final int neuronCount) {
		this.neuronCount = neuronCount;
	}

	/**
	 * Set the threshold array. This does not modify any of the other values in
	 * the network, it just sets the threshold array. If you want to change the
	 * structure of the neural network you should use the pruning classes.
	 * 
	 * @param d
	 *            The new threshold array.
	 */
	public void setThreshold(final double[] d) {
		this.threshold = d;
	}

	/**
	 * Set the specified threshold value.
	 * 
	 * @param index
	 *            The threshold value to set.
	 * @param d
	 *            The value to set the threshold to.
	 */
	public void setThreshold(final int index, final double d) {
		if (!hasThreshold()) {
			final String str = 
				"Attempting to set threshold on a thresholdless layer.";
			if (BasicLayer.LOGGER.isErrorEnabled()) {
				BasicLayer.LOGGER.error(str);
			}
			throw new NeuralNetworkError(str);
		}
		this.threshold[index] = d;
	}

	/**
	 * Set the x coordinate for this layer. The coordinates are used when the
	 * layer must be displayed in a GUI situation.
	 * 
	 * @param x
	 *            The x-coordinate.
	 */
	public void setX(final int x) {
		this.x = x;
	}

	/**
	 * Set the y coordinate for this layer. The coordinates are used when the
	 * layer must be displayed in a GUI situation.
	 * 
	 * @param y
	 *            The y-coordinate.
	 */
	public void setY(final int y) {
		this.y = y;
	}

	/**
	 * @return This object as a string.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append("(");
		result.append(getID());
		result.append(")");
		result.append(": neuronCount=");
		result.append(this.neuronCount);
		result.append(']');
		return result.toString();
	}

}
