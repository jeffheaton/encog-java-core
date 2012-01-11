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
package org.encog.app.quant.util;

import java.util.ArrayList;
import java.util.List;

import org.encog.util.EngineArray;

/**
 * A buffer of bar segments.
 */
public class BarBuffer {

	/**
	 * The bar data loaded.
	 */
	private final List<double[]> data = new ArrayList<double[]>();

	/**
	 * The number of periods.
	 */
	private final int periods;

	/**
	 * Construct the object.
	 * 
	 * @param thePeriods
	 *            The number of periods.
	 */
	public BarBuffer(final int thePeriods) {
		this.periods = thePeriods;
	}

	/**
	 * Add a bar.
	 * 
	 * @param d
	 *            The bar data.
	 */
	public final void add(final double d) {
		final double[] da = new double[1];
		da[0] = d;
		add(da);
	}

	/**
	 * Add a bar.
	 * 
	 * @param d
	 *            The bar data.
	 */
	public final void add(final double[] d) {
		this.data.add(0, EngineArray.arrayCopy(d));
		if (this.data.size() > this.periods) {
			this.data.remove(this.data.size() - 1);
		}
	}

	/**
	 * Average all of the bars.
	 * 
	 * @param idx
	 *            The bar index to average.
	 * @return The average.
	 */
	public final double average(final int idx) {
		double total = 0;
		for (int i = 0; i < this.data.size(); i++) {
			final double[] d = this.data.get(i);
			total += d[idx];
		}

		return total / this.data.size();
	}

	/**
	 * Get the average gain.
	 * 
	 * @param idx
	 *            The field to get the average gain for.
	 * @return The average gain.
	 */
	public final double averageGain(final int idx) {
		double total = 0;
		int count = 0;
		for (int i = 0; i < this.data.size() - 1; i++) {
			final double[] today = this.data.get(i);
			final double[] yesterday = this.data.get(i + 1);
			final double diff = today[idx] - yesterday[idx];
			if (diff > 0) {
				total += diff;
			}
			count++;
		}

		if (count == 0) {
			return 0;
		} else {
			return total / count;
		}
	}

	/**
	 * Get the average loss.
	 * 
	 * @param idx
	 *            The index to check for.
	 * @return The average loss.
	 */
	public final double averageLoss(final int idx) {
		double total = 0;
		int count = 0;
		for (int i = 0; i < this.data.size() - 1; i++) {
			final double[] today = this.data.get(i);
			final double[] yesterday = this.data.get(i + 1);
			final double diff = today[idx] - yesterday[idx];
			if (diff < 0) {
				total += Math.abs(diff);
			}
			count++;
		}

		if (count == 0) {
			return 0;
		} else {
			return total / count;
		}
	}

	/**
	 * @return The data.
	 */
	public final List<double[]> getData() {
		return this.data;
	}

	/**
	 * Determine if the buffer is full.
	 * @return True if the buffer is full.
	 */
	public final boolean getFull() {
		return this.data.size() >= this.periods;
	}

	/**
	 * Get the max for the specified index.
	 * 
	 * @param idx
	 *            The index to check.
	 * @return The max.
	 */
	public final double max(final int idx) {
		double result = Double.MIN_VALUE;

		for (final double[] d : this.data) {
			result = Math.max(d[idx], result);
		}
		return result;
	}

	/**
	 * Get the min for the specified index.
	 * 
	 * @param idx
	 *            The index to check.
	 * @return The min.
	 */
	public final double min(final int idx) {
		double result = Double.MAX_VALUE;

		for (final double[] d : this.data) {
			result = Math.min(d[idx], result);
		}
		return result;
	}

	/**
	 * Pop (and remove) the oldest bar in the buffer.
	 * 
	 * @return The oldest bar in the buffer.
	 */
	public final double[] pop() {
		if (this.data.size() == 0) {
			return null;
		}

		final int idx = this.data.size() - 1;
		final double[] result = this.data.get(idx);
		this.data.remove(idx);
		return result;
	}

}
