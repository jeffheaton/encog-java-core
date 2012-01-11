/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.neural.som.training.basic.neighborhood;

/**
 * A very simple neighborhood function that will return 1.0 (full effect) for
 * the winning neuron, and 0.0 (no change) for everything else.
 * 
 * @author jheaton
 * 
 */
public class NeighborhoodSingle implements NeighborhoodFunction {


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
