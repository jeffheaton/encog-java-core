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
package org.encog.util.normalize.input;

import java.io.Serializable;
import java.util.Iterator;

import org.encog.ml.data.MLDataPair;

/**
 * Simple holder class used internally for Encog.
 * Used as a holder for a:
 * 
 *  NeuralDataPair
 *  Iterator
 *  InputFieldNeuralDataSet
 */
public class MLDataFieldHolder implements Serializable {
	
	/**
	 * A neural data pair.
	 */
	private MLDataPair pair;
	
	/**
	 * An iterator.
	 */
	private final Iterator<MLDataPair> iterator;
	
	/**
	 * A field.
	 */
	private final InputFieldMLDataSet field;

	/**
	 * Construct the class.
	 * @param iterator An iterator.
	 * @param field A field.
	 */
	public MLDataFieldHolder(final Iterator<MLDataPair> iterator,
			final InputFieldMLDataSet field) {
		super();
		this.iterator = iterator;
		this.field = field;
	}

	/**
	 * @return The field.
	 */
	public InputFieldMLDataSet getField() {
		return this.field;
	}

	/**
	 * @return The iterator.
	 */
	public Iterator<MLDataPair> getIterator() {
		return this.iterator;
	}

	/**
	 * @return The pair.
	 */
	public MLDataPair getPair() {
		return this.pair;
	}

	/**
	 * Obtain the next pair.
	 */
	public void obtainPair() {
		this.pair = this.iterator.next();
	}

	/**
	 * Set the pair.
	 * @param pair The pair.
	 */
	public void setPair(final MLDataPair pair) {
		this.pair = pair;
	}
}
