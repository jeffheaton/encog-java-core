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
package org.encog.neural.networks.layers;

import java.util.Collection;
import java.util.List;

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.persist.EncogPersistedObject;

/**
 * This interface defines all necessary methods for a neural network layer.
 * @author jheaton
 */
public interface Layer extends Cloneable, EncogPersistedObject  {
	
	/**
	 * Compute the output for this layer.
	 * @param pattern The input pattern.
	 * @return The output from this layer.
	 */
	NeuralData compute(final NeuralData pattern);
	void process(final NeuralData pattern);

	/**
	 * @return The neuron count.
	 */
	int getNeuronCount();
	Collection<Layer> getNextLayers();
	List<Synapse> getNext();
	ActivationFunction getActivationFunction();
	
	/**
	 * Set the neuron count, this will NOT adjust the synapses, or thresholds
	 * other code must do that.
	 * @param neuronCount The new neuron count
	 */
	void setNeuronCount(int neuronCount);
	
	void addNext(Layer next);
	void addNext(Layer next, SynapseType type);
	NeuralData recur();
	Object clone();
	
	double getThreshold(int index);
	void setThreshold(int index,double d);
	void setThreshold(double[] d);
	double[] getThreshold();
	boolean hasThreshold();
	void addSynapse(Synapse synapse);
	int getX();
	int getY();
	void setX(int x);
	void setY(int y);
	boolean isConnectedTo(Layer layer);
}
