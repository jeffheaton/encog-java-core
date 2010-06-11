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

package org.encog.neural.networks.training.competitive.neighborhood;

import org.encog.mathutil.rbf.RadialBasisFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A neighborhood function based on the Gaussian function.
 * 
 * @author jheaton
 */
public class NeighborhoodGaussian implements NeighborhoodFunction {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The radial basis function (RBF) to use to calculate the training falloff
	 * from the best neuron.
	 */
	private final RadialBasisFunction radial;

	/**
	 * Construct the neighborhood function with the specified radial function.
	 * Generally this will be a Gaussian function but any RBF should do.
	 * 
	 * @param radial
	 *            The radial basis function to use.
	 */
	public NeighborhoodGaussian(final RadialBasisFunction radial) {
		this.radial = radial;
	}

	/**
	 * Determine how much the current neuron should be affected by training
	 * based on its proximity to the winning neuron.
	 * 
	 * @param currentNeuron
	 *            THe current neuron being evaluated.
	 * @param bestNeuron
	 *            The winning neuron.
	 * @return The ratio for this neuron's adjustment.
	 */
	public double function(final int currentNeuron, final int bestNeuron) {
		return this.radial.calculate(currentNeuron - bestNeuron);
	}

	/**
	 * @return The radius.
	 */
	public double getRadius() {
		return this.radial.getWidth();
	}

	/**
	 * Set the radius.
	 * @param radius The new radius.
	 */
	public void setRadius(final double radius) {
		this.radial.setWidth(radius);
	}

}
