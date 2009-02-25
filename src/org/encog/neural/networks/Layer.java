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
package org.encog.neural.networks;

import org.encog.matrix.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.layers.Synapse;

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
	NeuralData compute(final NeuralData pattern);

	/**
	 * Set the previous layer.
	 * @param layer The previous layer.
	 */
	void setPrevious(Layer layer);
	
	/**
	 * @return Get the previous layer.
	 */
	Layer getPrevious();

	/**
	 * Set the next layer.
	 * @param layer The next layer.
	 */
	void setNext(Layer layer);

	/**
	 * @return The neuron count.
	 */
	int getNeuronCount();

	/**
	 * @return True if this is an input layer.
	 */
	boolean isInput();

	/** 
	 * @return True if this is a hidden layer.
	 */
	boolean isHidden();

	/**
	 * Reset the weight matrix to random values.
	 */
	void reset();

	/**
	 * @return Is this an output matrix.
	 */
	boolean isOutput();

	/**
	 * @return The next layer.
	 */
	Layer getNext();
	
	Synapse getSynapse();
}
