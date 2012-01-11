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
package org.encog.mathutil.randomize;

import org.encog.mathutil.matrices.Matrix;
import org.encog.ml.MLMethod;

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
	void randomize(MLMethod network);

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
	 * Randomize the 2d array based on an array, modify the array. Previous
	 * values may be used, or they may be discarded, depending on the
	 * randomizer.
	 *
	 * @param d
	 *            An array to randomize.
	 */
	void randomize(double[][] d);

	/**
	 * Randomize the matrix based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 *
	 * @param m
	 *            A matrix to randomize.
	 */
	void randomize(Matrix m);
	
	/**
	 * Randomize an array.
	 * @param d The array to randomize.
	 * @param begin The beginning element.
	 * @param size The size of the array.
	 */
	void randomize(final double[] d, int begin, int size);

}
