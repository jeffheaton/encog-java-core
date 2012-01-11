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

import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.output.BasicOutputField;

/**
 * An output field that uses the "on of" technique to represent input data. For
 * example, if there were five nominal items, or classes, given then each one
 * would be represented by a single output neuron that would be on or off.
 * 
 * Often the OutputEquilateral class is a better choice to represent nominal
 * items.
 * 
 */
public class OutputOneOf extends BasicOutputField {

	/**
	 * The nominal items to represent.
	 */
	private final List<NominalItem> items = new ArrayList<NominalItem>();

	/**
	 * What is the true value, often just "1".
	 */
	private double trueValue;

	/**
	 * What is the true value, often just "0" or "-1".
	 */
	private double falseValue;

	/**
	 * Default constructor for reflection.
	 */
	public OutputOneOf() {
		this(1,-1);
	}

	/**
	 * Construct a one-of field and specify the true and false value.
	 * 
	 * @param trueValue
	 *            The true value.
	 * @param falseValue
	 *            The false value.
	 */
	public OutputOneOf(final double trueValue, final double falseValue) {
		this.trueValue = trueValue;
		this.falseValue = falseValue;
	}

	/**
	 * Add a nominal value specifying a single value, the high and low values
	 * will be 0.5 below and 0.5 above.
	 * 
	 * @param inputField The input field to use.
	 * @param value The value to calculate the high and low values off of.
	 */
	public void addItem(final InputField inputField, final double value) {
		addItem(inputField, value - 0.5, value + 0.5);
	}

	/**
	 * Add a nominal item, specify the low and high values.
	 * 
	 * @param inputField The input field to base everything from.
	 * @param low The high value for this nominal item.
	 * @param high The low value for this nominal item.
	 */
	public void addItem(final InputField inputField, final double low,
			final double high) {
		final NominalItem item = new NominalItem(inputField, low, high);
		this.items.add(item);
	}

	/**
	 * Calculate the value for the specified subfield.
	 * 
	 * @param subfield
	 *            The subfield to calculate for.
	 * @return The calculated value for this field.
	 */
	public double calculate(final int subfield) {
		final NominalItem item = this.items.get(subfield);
		return item.isInRange() ? this.trueValue : this.falseValue;
	}

	/**
	 * @return The false value.
	 */
	public double getFalseValue() {
		return this.falseValue;
	}

	/**
	 * @return The number of subfields, or nominal classes.
	 */
	public int getSubfieldCount() {
		return this.items.size();
	}

	/**
	 * @return The true value.
	 */
	public double getTrueValue() {
		return this.trueValue;
	}

	/**
	 * Not needed for this sort of output field.
	 */
	public void rowInit() {
	}

}
