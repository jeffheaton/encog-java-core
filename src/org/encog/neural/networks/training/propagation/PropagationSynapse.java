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
package org.encog.neural.networks.training.propagation;

import org.encog.matrix.Matrix;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BackpropagationLayer: The back propagation training algorithm requires
 * training data to be stored for each of the layers. The Backpropagation class
 * creates a BackpropagationLayer object for each of the layers in the neural
 * network that it is training.
 */

public class PropagationSynapse {


	/**
	 * Accumulate the error deltas for each weight matrix and bias value.
	 */
	private Matrix accMatrixGradients;


	/**
	 * Hold the previous matrix deltas so that "momentum" and other methods can be implemented.
	 * This handles both weights and thresholds.
	 */
	private Matrix lastMatrixGradients;

	/**
	 * The actual layer that this training layer corresponds to.
	 */
	private final Synapse synapse;
	
	private final Matrix deltas;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	

	/**
	 * Construct a BackpropagationLayer object that corresponds to a specific
	 * neuron layer.
	 * 
	 * @param backpropagation
	 *            The back propagation training object.
	 * @param layer
	 *            The layer that this object corresponds to.
	 */
	public PropagationSynapse(final Synapse synapse) {		
		this.synapse = synapse;
		int fromCount = synapse.getFromNeuronCount();
		int toCount = synapse.getToNeuronCount();
		
		this.accMatrixGradients = new Matrix(fromCount,toCount);
		this.lastMatrixGradients = new Matrix(fromCount,toCount);
		this.deltas = new Matrix(fromCount,toCount);
	}

	/**
	 * Accumulate a matrix delta.
	 * 
	 * @param i1
	 *            The matrix row.
	 * @param i2
	 *            The matrix column.
	 * @param value
	 *            The delta value.
	 */
	public void accumulateMatrixDelta(final int i1, final int i2,
			final double value) {
		this.accMatrixGradients.add(i1, i2, value);
	}

	public Matrix getAccMatrixGradients() {
		return accMatrixGradients;
	}

	public Synapse getSynapse() {
		return synapse;
	}
	
	public Matrix getLastMatrixGradients() {
		return lastMatrixGradients;
	}

	public void setLastMatrixGradients(Matrix d) {
		this.lastMatrixGradients = d;
	}

	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[PropagationSynapse:");
		result.append(this.synapse.toString());
		result.append("]");
		return result.toString();
	}

	public Matrix getDeltas() {
		return deltas;
	}
	
	

}
