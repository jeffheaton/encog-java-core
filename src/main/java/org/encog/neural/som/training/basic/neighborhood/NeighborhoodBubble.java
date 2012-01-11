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
 * A neighborhood function that uses a simple bubble. A radius is defined, and
 * any neuron that is plus or minus that width from the winning neuron will be
 * updated as a result of training.
 * 
 * @author jheaton
 * 
 */
public class NeighborhoodBubble implements NeighborhoodFunction {

	/**
	 * The radius of the bubble.
	 */
	private double radius;

	/**
	 * Create a bubble neighborhood function that will return 1.0 (full update)
	 * for any neuron that is plus or minus the width distance from the winning
	 * neuron.
	 * 
	 * @param radius
	 *            The width of the bubble, this is the distance that the neuron
	 *            can be from the winning neuron. The true width, across the
	 *            bubble, is actually two times this parameter.
	 */
	public NeighborhoodBubble(final int radius) {
		this.radius = radius;
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
	@Override
	public final double function(final int currentNeuron, 
			final int bestNeuron) {
		final int distance = Math.abs(bestNeuron - currentNeuron);
		if (distance <= this.radius) {
			return 1.0;
		} else {
			return 0.0;
		}
	}

	/**
	 * @return The radius.
	 */
	@Override
	public final double getRadius() {
		return this.radius;
	}

	/**
	 * Set the radius.
	 * 
	 * @param radius
	 *            The new radius.
	 */
	@Override
	public final void setRadius(final double radius) {
		this.radius = radius;
	}

}
