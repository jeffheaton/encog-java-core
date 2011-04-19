/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.neural.networks;

import org.encog.neural.data.MLDataArray;

/**
 * Used to map one neural data object to another. Useful for a BAM network.
 * 
 */
public class NeuralDataMapping {

	/**
	 * Copy from one object to the other.
	 * 
	 * @param source
	 *            The source object.
	 * @param target
	 *            The target object.
	 */
	public static void copy(final NeuralDataMapping source,
			final NeuralDataMapping target) {
		for (int i = 0; i < source.getFrom().size(); i++) {
			target.getFrom().setData(i, source.getFrom().getData(i));
		}

		for (int i = 0; i < source.getTo().size(); i++) {
			target.getTo().setData(i, source.getTo().getData(i));
		}
	}

	/**
	 * The source data.
	 */
	private MLDataArray from;

	/**
	 * The target data.
	 */
	private MLDataArray to;

	/**
	 * Construct the neural data mapping class, with null values.
	 */
	public NeuralDataMapping() {
		this.from = null;
		this.to = null;
	}

	/**
	 * Construct the neural data mapping class with the specified values.
	 * 
	 * @param from
	 *            The source data.
	 * @param to
	 *            The target data.
	 */
	public NeuralDataMapping(final MLDataArray from, final MLDataArray to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * @return The "from" data.
	 */
	public MLDataArray getFrom() {
		return this.from;
	}

	/**
	 * @return The "to" data.
	 */
	public MLDataArray getTo() {
		return this.to;
	}

	/**
	 * Set the from data.
	 * 
	 * @param from
	 *            The from data.
	 */
	public void setFrom(final MLDataArray from) {
		this.from = from;
	}

	/**
	 * Set the target data.
	 * 
	 * @param to
	 *            The target data.
	 */
	public void setTo(final MLDataArray to) {
		this.to = to;
	}
}
