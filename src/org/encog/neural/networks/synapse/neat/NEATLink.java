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

package org.encog.neural.networks.synapse.neat;

import java.io.Serializable;

import org.encog.persist.annotations.EGReference;

/**
 * Implements a link between two NEAT neurons.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATLink implements Serializable {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -4117045705080951946L;

	/**
	 * The source neuron.
	 */
	@EGReference
	private NEATNeuron fromNeuron;

	/**
	 * Is this link recurrent.
	 */
	private boolean recurrent;

	/**
	 * The target neuron.
	 */
	@EGReference
	private NEATNeuron toNeuron;

	/**
	 * The weight between the two neurons.
	 */
	private double weight;

	/**
	 * Default constructor, used mainly for persistance.
	 */
	public NEATLink() {

	}

	/**
	 * Construct a NEAT link.
	 * 
	 * @param weight
	 *            The weight between the two neurons.
	 * @param fromNeuron
	 *            The source neuron.
	 * @param toNeuron
	 *            The target neuron.
	 * @param recurrent
	 *            Is this a recurrent link.
	 */
	public NEATLink(final double weight, final NEATNeuron fromNeuron,
			final NEATNeuron toNeuron, final boolean recurrent) {
		this.weight = weight;
		this.fromNeuron = fromNeuron;
		this.toNeuron = toNeuron;
		this.recurrent = recurrent;
	}

	/**
	 * @return The source neuron.
	 */
	public NEATNeuron getFromNeuron() {
		return this.fromNeuron;
	}

	/**
	 * @return The target neuron.
	 */
	public NEATNeuron getToNeuron() {
		return this.toNeuron;
	}

	/**
	 * @return The weight of the link.
	 */
	public double getWeight() {
		return this.weight;
	}

	/**
	 * @return True if this is a recurrent link.
	 */
	public boolean isRecurrent() {
		return this.recurrent;
	}
}
