/*
 * Encog Artificial Intelligence Framework v1.x
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
package org.encog.neural.networks.layers;

import java.util.Collection;
import java.util.List;

import org.encog.matrix.Matrix;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;

/**
 * This interface defines all necessary methods for a neural network layer.
 * @author jheaton
 */
public interface Layer {
	
	/**
	 * Compute the output for this layer.
	 * @param pattern The input pattern.
	 * @return The output from this layer.
	 */
	void compute(final NeuralData pattern);

	/**
	 * @return The neuron count.
	 */
	int getNeuronCount();

	/**
	 * Reset the weight matrix to random values.
	 */
	void reset();
	
	Collection<Layer> getNextLayers();
	
	public List<Synapse> getNext();

	
	ActivationFunction getActivationFunction();
	
	void setNeuronCount(int neuronCount);
	
	void addNext(Layer next);
	void addNext(Layer next, SynapseType type);
}
