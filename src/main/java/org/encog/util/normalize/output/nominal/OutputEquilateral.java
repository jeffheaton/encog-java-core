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
package org.encog.util.normalize.output.nominal;

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.Equilateral;
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.output.BasicOutputField;

/**
 * Allows nominal items to be encoded using the equilateral method. This maps
 * the nominal items into an array of input or output values minus 1. This can
 * sometimes provide a more accurate representation than the "one of" method.
 * Based on: Guiver and Klimasauskas (1991).
 */
public class OutputEquilateral extends BasicOutputField {

	/**
	 * THe nominal items.
	 */
	private final List<NominalItem> items = new ArrayList<NominalItem>();

	/**
	 * The current equilateral matrix.
	 */
	private Equilateral equilateral;

	/**
	 * The current value, which nominal item is selected.
	 */
	private int currentValue;

	/**
	 * The high value to map into.
	 */
	private double high;

	/**
	 * THe low value to map into.
	 */
	private double low;

	/**
	 * Prodvide a default constructor for reflection.
	 */
	public OutputEquilateral() {
		this(-1,1);
	}

	/**
	 * Create an equilateral output field with the specified high and low output
	 * values. These will often be 0 to 1 or -1 to 1.
	 * 
	 * @param high
	 *            The high output value.
	 * @param low
	 * 				The low output value.
	 */
	public OutputEquilateral(final double low, final double high) {
		this.high = high;
		this.low = low;
	}

	/**
	 * Add a nominal value based on a single value.  This creates a 0.1 range
	 * around this value.
	 * @param inputField The input field this is based on.
	 * @param value The value.
	 */
	public void addItem(final InputField inputField, final double value) {
		addItem(inputField, value - 0.1, value + 0.1);
	}

	/**
	 * Add a nominal item based on a range.
	 * @param inputField The input field to use.
	 * @param low The low value of the range.
	 * @param high The high value of the range.
	 */
	public void addItem(final InputField inputField, final double low,
			final double high) {
		final NominalItem item = new NominalItem(inputField, low, high);
		this.items.add(item);
	}

	/**
	 * Calculate the value for the specified subfield.
	 * @param subfield The subfield to calculate for.
	 * @return The calculated value.
	 */
	public double calculate(final int subfield) {
		return this.equilateral.encode(this.currentValue)[subfield];
	}

	/**
	 * @return The equalateral table being used.
	 */
	public Equilateral getEquilateral() {
		return this.equilateral;
	}

	/**
	 * @return The high value of the range.
	 */
	public double getHigh() {
		return this.high;
	}

	/**
	 * @return The low value of the range.
	 */
	public double getLow() {
		return this.low;
	}

	/**
	 * This is the total number of nominal items minus 1.
	 * 
	 * @return The number of subfields.
	 */
	public int getSubfieldCount() {
		return this.items.size() - 1;
	}

	/**
	 * Determine which item's index is the value.
	 */
	public void rowInit() {
		for (int i = 0; i < this.items.size(); i++) {
			final NominalItem item = this.items.get(i);
			if (item.isInRange()) {
				this.currentValue = i;
				break;
			}
		}

		if (this.equilateral == null) {
			this.equilateral = new Equilateral(this.items.size(), this.high,
					this.low);
		}
	}
}
