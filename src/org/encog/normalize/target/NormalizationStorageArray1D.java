/*
 * Encog(tm) Core v2.4
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

package org.encog.normalize.target;

/**
 * Output the normalized data to a 1D array.
 */
public class NormalizationStorageArray1D implements NormalizationStorage {

	/**
	 * The array to store to.
	 */
	private final double[] array;
	
	/**
	 * The current index.
	 */
	private int currentIndex;

	/**
	 * Construct an object to store to a 2D array.
	 * @param array The array to store to.
	 */
	public NormalizationStorageArray1D(final double[] array) {
		this.array = array;
		this.currentIndex = 0;
	}

	/**
	 * Not needed for this storage type.
	 */
	public void close() {

	}

	/**
	 * Not needed for this storage type.
	 */
	public void open() {

	}

	/**
	 * Write an array.
	 * 
	 * @param data
	 *            The data to write.
	 * @param inputCount
	 *            How much of the data is input.
	 */
	public void write(final double[] data, final int inputCount) {
		this.array[this.currentIndex++] = data[0];
	}

}
