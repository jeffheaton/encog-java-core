/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.neural.networks.training.competitive.neighborhood;

import org.encog.engine.network.rbf.RadialBasisFunction;
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
