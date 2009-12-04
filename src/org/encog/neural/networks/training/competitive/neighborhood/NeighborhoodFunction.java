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
package org.encog.neural.networks.training.competitive.neighborhood;

/**
 * Defines how a neighborhood function should work in competitive training.
 * This is most often used in the training process for a self-organizing map.
 * This function determines to what degree the training should take place on
 * a neuron, based on its proximity to the "winning" neuron.
 * @author jheaton
 *
 */
public interface NeighborhoodFunction {
	
	/**
	 * Determine how much the current neuron should be affected by 
	 * training based on its proximity to the winning neuron.
	 * @param currentNeuron THe current neuron being evaluated.
	 * @param bestNeuron The winning neuron.
	 * @return The ratio for this neuron's adjustment.
	 */
	double function(int currentNeuron, int bestNeuron);
	
	void setRadius(double radius);
	
	double getRadius();

}
