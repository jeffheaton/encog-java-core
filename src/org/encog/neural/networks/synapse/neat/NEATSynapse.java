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

import org.encog.mathutil.matrices.Matrix;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.neural.NeuralNetworkError;
import org.encog.persist.Persistor;

public class NEATSynapse implements Synapse {

	private Layer fromLayer;
	private Layer toLayer;
	private List<NEATNeuron> neurons = new ArrayList<NEATNeuron>();
	private int networkDepth;
	private boolean snapshot = false;
	private ActivationFunction activationFunction;

	public NEATSynapse(BasicLayer fromLayer, BasicLayer toLayer,
			List<NEATNeuron> neurons, ActivationFunction activationFunction, int networkDepth) {
		this.fromLayer = fromLayer;
		this.toLayer = toLayer;
		this.neurons.addAll(neurons);
		this.networkDepth = networkDepth;
		this.activationFunction = activationFunction;
	}

	/**
	 * @return A clone of this object.
	 */
	public Object clone() {
		return null;
	}

	/**
	 * Compute the output from this synapse.
	 * 
	 * @param input
	 *            The input to this synapse.
	 * @return The output from this synapse.
	 */
	public NeuralData compute(NeuralData input) {
		NeuralData result = new BasicNeuralData(this.getToNeuronCount());

		int flushCount = 1;

		if (this.snapshot) {
			flushCount = this.networkDepth;
		}

		// iterate through the network FlushCount times
		for (int i = 0; i < flushCount; ++i) {
			int outputIndex = 0;
			int index = 0;

			result.clear();

			// populate the input neurons
			while (this.neurons.get(index).getNeuronType() == NEATNeuronType.Input) {
				this.neurons.get(index).setOutput(
						input.getData(index));

				index++;
			}

			// set the bias neuron
			this.neurons.get(index++).setOutput(1);

			while (index < this.neurons.size()) {
				
				NEATNeuron currentNeuron = this.neurons.get(index);
				
				double sum = 0;

				for(NEATLink link: currentNeuron.getInboundLinks()) {
					double weight = link.getWeight();
					double neuronOutput = link.getFromNeuron().getOutput();
					sum += weight * neuronOutput;
				}

				double[] d = new double[1];
				d[0] = sum/currentNeuron.getActivationResponse();
				activationFunction.activationFunction(d);

				this.neurons.get(index).setOutput(d[0]);

				if (currentNeuron.getNeuronType() == NEATNeuronType.Output) {
					result.setData(outputIndex++, currentNeuron.getOutput());
				}
				index++;
			}
		}

		if (snapshot) {
			for (NEATNeuron neuron : this.neurons) {
				neuron.setOutput(0);
			}
		}

		return result;
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
	 * Get the weight and threshold matrix.
	 * 
	 * @return The weight and threshold matrix.
	 */
	public Matrix getMatrix() {
		return null;
	}

	/**
	 * Get the size of the matrix, or zero if one is not defined.
	 * 
	 * @return The size of the matrix.
	 */
	public int getMatrixSize() {
		return 0;
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
	 * @return The type of synapse that this is.
	 */
	public SynapseType getType() {
		return null;
	}

	/**
	 * @return True if this is a self-connected synapse. That is, the from and
	 *         to layers are the same.
	 */
	public boolean isSelfConnected() {
		return false;
	}

	/**
	 * @return True if the weights for this synapse can be modified.
	 */
	public boolean isTeachable() {
		return false;
	}

	/**
	 * Set the from layer for this synapse.
	 * 
	 * @param fromLayer
	 *            The from layer for this synapse.
	 */
	public void setFromLayer(Layer fromLayer) {
		this.fromLayer = fromLayer;
	}

	/**
	 * Assign a new weight and threshold matrix to this layer.
	 * 
	 * @param matrix
	 *            The new matrix.
	 */
	public void setMatrix(final Matrix matrix) {
		throw new NeuralNetworkError(
				"Neat synapse cannot have a simple matrix.");
	}

	/**
	 * Set the target layer from this synapse.
	 * 
	 * @param toLayer
	 *            The target layer from this synapse.
	 */
	public void setToLayer(Layer toLayer) {
		this.toLayer = toLayer;
	}

	public String getDescription() {
		return null;
	}

	public String getName() {
		return null;
	}

	public void setName(String name) {

	}

	public void setDescription(String description) {

	}

	public Persistor createPersistor() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSnapshot() {
		return snapshot;
	}

	public void setSnapshot(boolean snapshot) {
		this.snapshot = snapshot;
	}

	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}

	public void setActivationFunction(ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}

	public List<NEATNeuron> getNeurons() {
		return neurons;
	}

	public int getNetworkDepth() {
		return networkDepth;
	}
	
	

}
