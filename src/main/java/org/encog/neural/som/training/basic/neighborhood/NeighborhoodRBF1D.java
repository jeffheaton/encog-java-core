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

import org.encog.mathutil.rbf.GaussianFunction;
import org.encog.mathutil.rbf.InverseMultiquadricFunction;
import org.encog.mathutil.rbf.MexicanHatFunction;
import org.encog.mathutil.rbf.MultiquadricFunction;
import org.encog.mathutil.rbf.RBFEnum;
import org.encog.mathutil.rbf.RadialBasisFunction;
import org.encog.neural.NeuralNetworkError;

/**
 * A neighborhood function based on an RBF function.
 * 
 * @author jheaton
 */
public class NeighborhoodRBF1D implements NeighborhoodFunction {

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
	public NeighborhoodRBF1D(final RadialBasisFunction radial) {
		this.radial = radial;
	}
	
	/**
	 * Construct a 1d neighborhood function.
	 * @param type The RBF type to use.
	 */
	public NeighborhoodRBF1D(final RBFEnum type) {

		switch(type)
		{
			case Gaussian:
				this.radial = new GaussianFunction(1);
				break;
			case InverseMultiquadric:
				this.radial = new InverseMultiquadricFunction(1);
				break;
			case Multiquadric:
				this.radial = new MultiquadricFunction(1);
				break;
			case MexicanHat:
				this.radial = new MexicanHatFunction(1);
				break;		
			default:
				throw new NeuralNetworkError("Unknown RBF type: " + type.toString());
		}
		
		this.radial.setWidth(1.0);
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
		double[] d = new double[1];
		d[0] = currentNeuron - bestNeuron;
		return this.radial.calculate(d);
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
