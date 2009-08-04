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
package org.encog.neural.networks.logic;

import java.io.Serializable;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;

/**
 * Neural logic classes implement neural network logic for a variety
 * of neural network setups.
 */
public interface NeuralLogic extends Serializable {
	
	/**
	 * Compute the output for the BasicNetwork class.
	 * @param input The input to the network.
	 * @param useHolder The NeuralOutputHolder to use.
	 * @return The output from the network.
	 */
	NeuralData compute(NeuralData input,
			NeuralOutputHolder useHolder);
	
	/**
	 * Setup the network logic, read parameters from the network.
	 * @param network The network that this logic class belongs to.
	 */
	void init(BasicNetwork network);
}
