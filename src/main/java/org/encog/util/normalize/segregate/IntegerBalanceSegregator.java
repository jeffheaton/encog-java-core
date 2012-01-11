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
package org.encog.util.normalize.segregate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.util.normalize.DataNormalization;
import org.encog.util.normalize.input.InputField;

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
	private DataNormalization normalization;

	/**
	 * The input field.
	 */
	private InputField target;

	/**
	 * The count per each of the int values for the input field.
	 */
	private int count;

	/**
	 * The running totals.
	 */
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
