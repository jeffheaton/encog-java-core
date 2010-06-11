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

import org.encog.mathutil.rbf.GaussianFunctionMulti;
import org.encog.mathutil.rbf.RadialBasisFunctionMulti;

/**
 * Implements a multi-dimensional gaussian neighborhood function.  DO not
 * use this for a 1D gaussian, just use the NeighborhoodGaussian for that.
 *
 */
public class NeighborhoodGaussianMulti implements NeighborhoodFunction {

	/**
	 * The radial basis function to use.
	 */
	private final RadialBasisFunctionMulti rbf;
	
	/**
	 * The size of each dimension.
	 */
	private final int[] size;
	
	/**
	 * The displacement of each dimension, when mapping the dimensions
	 * to a 1d array.
	 */
	private int[] displacement;

	/**
	 * Construct a 2d neighborhood function based on the sizes for the
	 * x and y dimensions.
	 * @param x The size of the x-dimension.
	 * @param y The size of the y-dimension.
	 */
	public NeighborhoodGaussianMulti(final int x, final int y) {
		final int[] size = new int[2];
		size[0] = x;
		size[1] = y;

		final double[] centerArray = new double[2];
		centerArray[0] = 0;
		centerArray[1] = 0;

		final double[] widthArray = new double[2];
		widthArray[0] = 1;
		widthArray[1] = 1;

		final RadialBasisFunctionMulti rbf = new GaussianFunctionMulti(1,
				centerArray, widthArray);

		this.rbf = rbf;
		this.size = size;

		calculateDisplacement();
	}

	/**
	 * Construct a multi-dimensional neighborhood function.
	 * @param size The sizes of each dimension.
	 * @param rbf The multi-dimensional RBF to use.
	 */
	NeighborhoodGaussianMulti(final int[] size,
			final RadialBasisFunctionMulti rbf) {
		this.rbf = rbf;
		this.size = size;
		calculateDisplacement();
	}

	/**
	 * Calculate all of the displacement values.
	 */
	private void calculateDisplacement() {
		this.displacement = new int[this.size.length];
		for (int i = 0; i < this.size.length; i++) {
			int value;

			if (i == 0) {
				value = 0;
			} else if (i == 1) {
				value = this.size[0];
			} else {
				value = this.displacement[i - 1] * this.size[i - 1];
			}

			this.displacement[i] = value;
		}
	}

	/**
	 * Calculate the value for the multi RBF function.
	 * @param currentNeuron The current neuron.
	 * @param bestNeuron The best neuron.
	 * @return A percent that determines the amount of training the current
	 * neuron should get.  Usually 100% when it is the bestNeuron.
	 */
	public double function(final int currentNeuron, final int bestNeuron) {
		final double[] vector = new double[this.displacement.length];
		final int[] vectorCurrent = translateCoordinates(currentNeuron);
		final int[] vectorBest = translateCoordinates(bestNeuron);
		for (int i = 0; i < vectorCurrent.length; i++) {
			vector[i] = vectorCurrent[i] - vectorBest[i];
		}
		return this.rbf.calculate(vector);

	}

	/**
	 * @return The radius.
	 */
	public double getRadius() {
		return this.rbf.getWidth(0);
	}

	/**
	 * @return The RBF to use.
	 */
	public RadialBasisFunctionMulti getRBF() {
		return this.rbf;
	}

	/**
	 * Set the radius.
	 * @param radius The radius.
	 */
	public void setRadius(final double radius) {
		this.rbf.setWidth(radius);
	}

	/**
	 * Translate the specified index into a set of multi-dimensional
	 * coordinates that represent the same index.  This is how the
	 * multi-dimensional coordinates are translated into a one dimensional
	 * index for the input neurons.
	 * @param index The index to translate.
	 * @return The multi-dimensional coordinates.
	 */
	private int[] translateCoordinates(final int index) {
		final int[] result = new int[this.displacement.length];
		int countingIndex = index;

		for (int i = this.displacement.length - 1; i >= 0; i--) {
			int value;
			if (this.displacement[i] > 0) {
				value = countingIndex / this.displacement[i];
			} else {
				value = countingIndex;
			}

			countingIndex -= this.displacement[i] * value;
			result[i] = value;

		}

		return result;
	}

}
