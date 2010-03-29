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

/**
 * Specifies the type of synapse to be created.
 * @author jheaton
 *
 */
public enum SynapseType {
	/**
	 * OneToOne - Each neuron is connected to the same neuron number
	 * in the next layer.  The two layers must have the same number
	 * of neurons.
	 */
	OneToOne,
	
	/**
	 * Weighted - The neurons are connected between the two levels
	 * with weights.  These weights change during training.
	 */
	Weighted,
	
	/**
	 * Weightless - Every neuron is connected to every other neuron
	 * in the next layer, but there are no weights.
	 */
	Weightless,
	
	/**
	 * Direct - Input is simply passed directly to the next layer.
	 */
	Direct,
	
	/**
	 * NEAT - A synapse that contains a NEAT network.
	 */
	NEAT,
	
	/**
	 * Partial - Not all neurons may be connected.
	 */
	Partial,
	
	/**
	 * Normalize - A synapse that normalizes the data.  Used to implement
	 * a SOM.
	 */
	Normalize
}
