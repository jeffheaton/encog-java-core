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

package org.encog.mathutil.randomize;

import org.encog.mathutil.matrices.Matrix2D;
import org.encog.neural.networks.BasicNetwork;

/**
 * Defines the interface for a class that is capable of randomizing the weights
 * and bias values of a neural network.
 *
 * @author jheaton
 *
 */
public interface Randomizer {

	/**
	 * Randomize the synapses and bias values in the basic network based on an
	 * array, modify the array. Previous values may be used, or they may be
	 * discarded, depending on the randomizer.
	 *
	 * @param network
	 *            A network to randomize.
	 */
	void randomize(BasicNetwork network);

	/**
	 * Starting with the specified number, randomize it to the degree specified
	 * by this randomizer. This could be a totally new random number, or it
	 * could be based on the specified number.
	 *
	 * @param d
	 *            The number to randomize.
	 * @return A randomized number.
	 */
	double randomize(double d);

	/**
	 * Randomize the array based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 *
	 * @param d
	 *            An array to randomize.
	 */
	void randomize(double[] d);

	/**
	 * Randomize the array based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 *
	 * @param d
	 *            An array to randomize.
	 */
	void randomize(Double[] d);

	/**
	 * Randomize the 2d array based on an array, modify the array. Previous
	 * values may be used, or they may be discarded, depending on the
	 * randomizer.
	 *
	 * @param d
	 *            An array to randomize.
	 */
	void randomize(double[][] d);

	/**
	 * Randomize the 2d array based on an array, modify the array. Previous
	 * values may be used, or they may be discarded, depending on the
	 * randomizer.
	 *
	 * @param d
	 *            An array to randomize.
	 */
	void randomize(Double[][] d);

	/**
	 * Randomize the matrix based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 *
	 * @param m
	 *            A matrix to randomize.
	 */
	void randomize(Matrix2D m);

}
