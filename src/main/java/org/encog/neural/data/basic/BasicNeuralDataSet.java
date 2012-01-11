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
package org.encog.neural.data.basic;

import java.util.List;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.data.NeuralDataSet;

/**
 * This is an alias class for Encog 2.5 compatibility. This class aliases
 * BasicMLDataSet. Newer code should use BasicMLDataSet in place of this class.
 */
public class BasicNeuralDataSet extends BasicMLDataSet 
	implements NeuralDataSet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8916242559498280598L;

	/**
	 * Construct empty.
	 */
	public BasicNeuralDataSet() {
		super();
	}

	/**
	 * Construct from 2d arrays.
	 * 
	 * @param input
	 *            The input.
	 * @param ideal
	 *            The ideal.
	 */
	public BasicNeuralDataSet(final double[][] input, final double[][] ideal) {
		super(input, ideal);
	}

	/**
	 * Construct from another list.
	 * 
	 * @param theData
	 *            The other list.
	 */
	public BasicNeuralDataSet(final List<MLDataPair> theData) {
		super(theData);
	}

	/**
	 * Construct from another object.
	 * 
	 * @param set
	 *            The other object.
	 */
	public BasicNeuralDataSet(final NeuralDataSet set) {
		super(set);
	}

}
