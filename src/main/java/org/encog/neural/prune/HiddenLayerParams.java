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
package org.encog.neural.prune;

/**
 * Specifies the minimum and maximum neuron counts for a layer.
 * 
 * @author jheaton
 * 
 */
public class HiddenLayerParams {

	/**
	 * The minimum number of neurons on this layer.
	 */
	private final int min;

	/**
	 * The maximum number of neurons on this layer.
	 */
	private final int max;

	/**
	 * Construct a hidden layer param object with the specified min and max
	 * values.
	 * 
	 * @param min
	 *            The minimum number of neurons.
	 * @param max
	 *            The maximum number of neurons.
	 */
	public HiddenLayerParams(final int min, final int max) {
		super();
		this.min = min;
		this.max = max;
	}

	/**
	 * @return The maximum number of neurons.
	 */
	public final int getMax() {
		return this.max;
	}

	/**
	 * @return The minimum number of neurons.
	 */
	public final int getMin() {
		return this.min;
	}

}
