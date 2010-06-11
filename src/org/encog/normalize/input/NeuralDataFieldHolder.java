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

package org.encog.normalize.input;

import java.util.Iterator;

import org.encog.neural.data.NeuralDataPair;

/**
 * Simple holder class used internally for Encog.
 * Used as a holder for a:
 * 
 *  NeuralDataPair
 *  Iterator
 *  InputFieldNeuralDataSet
 */
public class NeuralDataFieldHolder {
	
	/**
	 * A neural data pair.
	 */
	private NeuralDataPair pair;
	
	/**
	 * An iterator.
	 */
	private final Iterator<NeuralDataPair> iterator;
	
	/**
	 * A field.
	 */
	private final InputFieldNeuralDataSet field;

	/**
	 * Construct the class.
	 * @param iterator An iterator.
	 * @param field A field.
	 */
	public NeuralDataFieldHolder(final Iterator<NeuralDataPair> iterator,
			final InputFieldNeuralDataSet field) {
		super();
		this.iterator = iterator;
		this.field = field;
	}

	/**
	 * @return The field.
	 */
	public InputFieldNeuralDataSet getField() {
		return this.field;
	}

	/**
	 * @return The iterator.
	 */
	public Iterator<NeuralDataPair> getIterator() {
		return this.iterator;
	}

	/**
	 * @return The pair.
	 */
	public NeuralDataPair getPair() {
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
	public void setPair(final NeuralDataPair pair) {
		this.pair = pair;
	}
}
