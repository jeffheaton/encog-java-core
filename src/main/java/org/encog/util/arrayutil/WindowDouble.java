/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.util.arrayutil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a simple sliding window. Arrays of doubles can be added
 * to the window. The sliding window will fill up to the specified size.
 * Additional entries will cause the oldest entries to fall off.
 * 
 */
public class WindowDouble {

	/**
	 * The size of the window.
	 */
	private int size;

	/**
	 * The data in the window.
	 */
	private List<double[]> data = new ArrayList<double[]>();

	/**
	 * Construct the window.
	 * 
	 * @param theSize
	 *            The size of the window.
	 */
	public WindowDouble(int theSize) {
		this.size = theSize;
	}

	/**
	 * Add an array to the window.
	 * 
	 * @param a
	 *            The array.
	 */
	public void add(double[] a) {
		this.data.add(0, a);
		while (this.data.size() > this.size) {
			this.data.remove(this.data.size() - 1);
		}
	}

	/**
	 * Clear the contents of the window.
	 */
	public void clear() {
		this.data.clear();
	}

	/**
	 * @return True, if the window is full.
	 */
	public boolean isFull() {
		return this.data.size() == this.size;
	}

	/**
	 * Calculate the max value, for the specified index, over all of the data in
	 * the window.
	 * 
	 * @param index
	 *            The index of the value to compare.
	 * @param starting
	 *            The starting position, inside the window to compare at.
	 * @return THe max value.
	 */
	public double calculateMax(int index, int starting) {
		double result = Double.NEGATIVE_INFINITY;

		for (int i = starting; i < this.data.size(); i++) {
			double[] a = this.data.get(i);
			result = Math.max(a[index], result);
		}

		return result;
	}

	/**
	 * Calculate the max value, for the specified index, over all of the data in
	 * the window.
	 * 
	 * @param index
	 *            The index of the value to compare.
	 * @param starting
	 *            The starting position, inside the window to compare at.
	 * @return THe max value.
	 */
	public double calculateMin(int index, int starting) {
		double result = Double.POSITIVE_INFINITY;

		for (int i = starting; i < this.data.size(); i++) {
			double[] a = this.data.get(i);
			result = Math.min(a[index], result);
		}

		return result;
	}

	/**
	 * Get the last value from the window. This is the most recent item added.
	 * 
	 * @return The last value from the window.
	 */
	public double[] getLast() {
		return this.data.get(0);
	}
}
