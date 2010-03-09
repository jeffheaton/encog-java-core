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

import java.util.ArrayList;
import java.util.List;

import org.encog.persist.annotations.EGAttribute;
import org.encog.persist.annotations.EGReferenceable;

/**
 * Implements a NEAT neuron.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
@EGReferenceable
public class NEATNeuron {

	private final double activationResponse;
	private final List<NEATLink> inboundLinks = new ArrayList<NEATLink>();
	@EGAttribute
	private final int neuronID;
	private final NEATNeuronType neuronType;
	private double output;
	private final List<NEATLink> outputboundLinks = new ArrayList<NEATLink>();
	@EGAttribute
	private final int posX;
	@EGAttribute
	private final int posY;
	@EGAttribute
	private final double splitX;
	@EGAttribute
	private final double splitY;
	private final double sumActivation;

	public NEATNeuron(final NEATNeuronType neuronType, final int neuronID,
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

	public double getActivationResponse() {
		return activationResponse;
	}

	public List<NEATLink> getInboundLinks() {
		return inboundLinks;
	}

	public int getNeuronID() {
		return neuronID;
	}

	public NEATNeuronType getNeuronType() {
		return neuronType;
	}

	public double getOutput() {
		return output;
	}

	public List<NEATLink> getOutputboundLinks() {
		return outputboundLinks;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public double getSplitX() {
		return splitX;
	}

	public double getSplitY() {
		return splitY;
	}

	public double getSumActivation() {
		return sumActivation;
	}

	public void setOutput(final double output) {
		this.output = output;

	}

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
		}
		result.append("]");
		return result.toString();

	}

}
