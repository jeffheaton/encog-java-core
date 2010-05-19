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

package org.encog.neural.networks.synapse.neat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.persist.annotations.EGAttribute;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.annotations.EGReferenceable;

/**
 * Implements a NEAT neuron. Neat neurons are of a specific type, defined by the
 * NEATNeuronType enum. Usually NEAT uses a sigmoid activation function. The
 * activation response is used to allow the slope of the sigmoid to be evolved.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
@EGReferenceable
public class NEATNeuron implements Serializable {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -2815145950124389743L;

	/**
	 * The activation response. This is evolved to allow NEAT to scale the slope
	 * of the activation function.
	 */
	@EGAttribute
	private double activationResponse;

	/**
	 * Inbound links to this neuron.
	 */
	private final List<NEATLink> inboundLinks = new ArrayList<NEATLink>();

	/**
	 * The neuron id.
	 */
	@EGAttribute
	private long neuronID;

	/**
	 * The type of neuron this is.
	 */
	@EGAttribute
	private NEATNeuronType neuronType;

	/**
	 * The output from the neuron.
	 */
	@EGAttribute
	private double output;

	/**
	 * The outbound links for this neuron.
	 */
	private List<NEATLink> outputboundLinks = new ArrayList<NEATLink>();

	/**
	 * The x-position of this neuron. Used to split links, as well as display.
	 */
	@EGAttribute
	private int posX;

	/**
	 * The y-position of this neuron. Used to split links, as well as display.
	 */
	@EGAttribute
	private int posY;

	/**
	 * The split value for X. Used to track splits.
	 */
	@EGAttribute
	private double splitX;

	/**
	 * The split value for Y. Used to track splits.
	 */
	@EGAttribute
	private double splitY;

	/**
	 * The sum activation.
	 */
	@EGIgnore
	private double sumActivation;

	/**
	 * Default constructor, used for persistance.
	 */
	public NEATNeuron() {

	}

	/**
	 * Construct a NEAT neuron.
	 * 
	 * @param neuronType
	 *            The type of neuron.
	 * @param neuronID
	 *            The id of the neuron.
	 * @param splitY
	 *            The split for y.
	 * @param splitX
	 *            THe split for x.
	 * @param activationResponse
	 *            The activation response.
	 */
	public NEATNeuron(final NEATNeuronType neuronType, final long neuronID,
			final double splitY, final double splitX,
			final double activationResponse) {
		this.neuronType = neuronType;
		this.neuronID = neuronID;
		this.splitY = splitY;
		this.splitX = splitX;
		this.activationResponse = activationResponse;
		posX = 0;
		posY = 0;
		output = 0;
		sumActivation = 0;
	}

	/**
	 * @return the activation response.
	 */
	public double getActivationResponse() {
		return activationResponse;
	}

	/**
	 * @return the inbound links.
	 */
	public List<NEATLink> getInboundLinks() {
		return inboundLinks;
	}

	/**
	 * @return The neuron id.
	 */
	public long getNeuronID() {
		return neuronID;
	}

	/**
	 * @return the neuron type.
	 */
	public NEATNeuronType getNeuronType() {
		return neuronType;
	}

	/**
	 * @return The output from this neuron.
	 */
	public double getOutput() {
		return output;
	}

	/**
	 * @return The outbound links.
	 */
	public List<NEATLink> getOutputboundLinks() {
		return outputboundLinks;
	}

	/**
	 * @return The x position.
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * @return The y position.
	 */
	public int getPosY() {
		return posY;
	}

	/**
	 * @return The split x.
	 */
	public double getSplitX() {
		return splitX;
	}

	/**
	 * @return The split y.
	 */
	public double getSplitY() {
		return splitY;
	}

	/**
	 * @return The sum activation.
	 */
	public double getSumActivation() {
		return sumActivation;
	}

	/**
	 * Set the output.
	 * 
	 * @param output
	 *            The output of the neuron.
	 */
	public void setOutput(final double output) {
		this.output = output;

	}

	/**
	 * @return A string representation of the neuron.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[NEATNeuron:id=");
		result.append(neuronID);
		result.append(",type=");
		switch (neuronType) {
		case Input:
			result.append("I");
			break;
		case Output:
			result.append("O");
			break;
		case Bias:
			result.append("B");
			break;
		case Hidden:
			result.append("H");
			break;
		default:
			result.append("Unknown");
		}
		result.append("]");
		return result.toString();
	}
}
