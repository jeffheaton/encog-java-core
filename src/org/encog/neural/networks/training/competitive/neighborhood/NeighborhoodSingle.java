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

package org.encog.neural.networks.training.competitive.neighborhood;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A very simple neighborhood function that will return 1.0 (full effect) for
 * the winning neuron, and 0.0 (no change) for everything else.
 * 
 * @author jheaton
 * 
 */
public class NeighborhoodSingle implements NeighborhoodFunction {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
		if (currentNeuron == bestNeuron) {
			return 1.0;
		} else {
			return 0.0;
		}
	}

	/**
	 * The radius for this neighborhood function is always 1.
	 * @return The radius.
	 */
	public double getRadius() {
		return 1;
	}

	/**
	 * Set the radius.  This type does not use a radius, so this has no effect.
	 * 
	 * @param radius
	 *            The radius.
	 */
	public void setRadius(final double radius) {
		// no effect on this type
	}

}
