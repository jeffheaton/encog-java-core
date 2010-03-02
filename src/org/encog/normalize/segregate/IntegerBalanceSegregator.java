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

package org.encog.normalize.segregate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.normalize.DataNormalization;
import org.encog.normalize.input.InputField;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.annotations.EGReference;

/**
 * Balance based on an input value. This allows you to make sure that one input
 * class does not saturate the training data. To do this, you specify the input
 * value to check and the number of occurrences of each integer value of this
 * field to allow.
 */
public class IntegerBalanceSegregator implements Segregator {

	/**
	 * The normalization object to use.
	 */
	@EGReference
	private DataNormalization normalization;

	/**
	 * The input field.
	 */
	@EGReference
	private InputField target;

	/**
	 * The count per each of the int values for the input field.
	 */
	private int count;

	/**
	 * The running totals.
	 */
	@EGIgnore
	private final Map<Integer, Integer> runningCounts = 
		new HashMap<Integer, Integer>();

	/**
	 * Construct an integer balance segregator.
	 * @param target The input field to use.
	 * @param count The number of each unique integer to allow.
	 */
	public IntegerBalanceSegregator(final InputField target, final int count) {
		this.target = target;
		this.count = count;
	}

	/**
	 * Default constructor. 
	 */
	public IntegerBalanceSegregator() {

	}

	/**
	 * @return A string that contains the counts for each group.
	 */
	public String dumpCounts() {
		final StringBuilder result = new StringBuilder();

		for (final Entry<Integer, Integer> entry : this.runningCounts
				.entrySet()) {
			result.append(entry.getKey());
			result.append(" -> ");
			result.append(entry.getValue());
			result.append(" count\n");
		}
		return result.toString();
	}

	/**
	 * @return The amout of data allowed by this segregator.
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * @return The normalization object used with this segregator.
	 */
	public DataNormalization getNormalization() {
		return this.normalization;
	}

	/**
	 * @return The current count for each group.
	 */
	public Map<Integer, Integer> getRunningCounts() {
		return this.runningCounts;
	}

	/**
	 * @return The input field being used.
	 */
	public InputField getTarget() {
		return this.target;
	}

	/**
	 * Init the segregator with the owning normalization object.
	 * 
	 * @param normalization
	 *            The data normalization object to use.
	 */
	public void init(final DataNormalization normalization) {
		this.normalization = normalization;

	}

	/**
	 * Init for a new pass.
	 */
	public void passInit() {
		this.runningCounts.clear();
	}

	/**
	 * Determine of the current row should be included.
	 * 
	 * @return True if the current row should be included.
	 */
	public boolean shouldInclude() {
		final int key = (int) this.target.getCurrentValue();
		int value = 0;
		if (this.runningCounts.containsKey(key)) {
			value = this.runningCounts.get(key);
		}

		if (value < this.count) {
			value++;
			this.runningCounts.put(key, value);
			return true;
		} else {
			return false;
		}
	}

}
