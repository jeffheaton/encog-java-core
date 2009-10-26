/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
package org.encog.normalize.output.nominal;

import java.util.ArrayList;
import java.util.List;

import org.encog.normalize.input.InputField;
import org.encog.normalize.output.BasicOutputField;
import org.encog.persist.annotations.EGIgnore;
import org.encog.util.math.Equilateral;

/**
 * Allows nominal items to be encoded using the equilateral method. This maps
 * the nominal items into an array of input or output values minus 1. This can
 * sometimes provide a more accurate representation than the "one of" method.
 * Based on: Guiver and Klimasauskas (1991).
 */
public class OutputEquilateral extends BasicOutputField {

	/**
	 * THe nomal items.
	 */
	private final List<NominalItem> items = new ArrayList<NominalItem>();

	/**
	 * The current equilateral matrix.
	 */
	@EGIgnore
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
	public OutputEquilateral(final double high, final double low) {
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
		addItem(inputField, value + 0.1, value - 0.1);
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
