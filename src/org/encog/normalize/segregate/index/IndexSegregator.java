/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.normalize.segregate.index;

import org.encog.normalize.DataNormalization;
import org.encog.normalize.segregate.Segregator;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.annotations.EGReference;

/**
 * The index segregator. An abstract class to build index based segregators off
 * of. An index segregator is used to segregate the data according to its index.
 * Nothing about the data is actually compared. This makes the index range
 * segregator very useful for breaking the data into training and validation
 * sets. For example, you could very easily determine that 70% of the data is
 * for training, and 30% for validation.
 */
public abstract class IndexSegregator implements Segregator {

	/**
	 * The current index.  Updated rows are processed.
	 */
	@EGIgnore
	private int currentIndex = 0;

	/**
	 * THe normalization object this belongs to.
	 */
	@EGReference
	private DataNormalization normalization;

	/**
	 * @return The current index.
	 */
	public int getCurrentIndex() {
		return this.currentIndex;
	}

	/**
	 * @return The normalization object this object will use.
	 */
	public DataNormalization getNormalization() {
		return this.normalization;
	}

	/**
	 * Setup this class with the specified normalization object.
	 * @param normalization Normalization object.
	 */
	public void init(final DataNormalization normalization) {
		this.normalization = normalization;
	}

	/**
	 * Used to increase the current index as data is processed.
	 */
	public void rollIndex() {
		this.currentIndex++;
	}

}
