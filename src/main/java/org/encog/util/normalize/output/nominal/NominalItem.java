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

import java.io.Serializable;

import org.encog.util.normalize.input.InputField;

/**
 * A nominal item.
 */
public class NominalItem implements Serializable {

	/**
	 * The low value for the range.
	 */
	private double low;

	/**
	 * The high value for the range.
	 */
	private double high;

	/**
	 * The input field used to verify against the range.
	 */
	private InputField inputField;

	/**
	 * Construct a empty range item.  Used mainly for reflection.
	 */
	public NominalItem() {

	}

	/**
	 * Create a nominal item.
	 * @param inputField The field that this item is based on.
	 * @param high The high value.
	 * @param low The low value.
	 */
	public NominalItem(final InputField inputField, final double low,
			final double high) {
		super();
		this.high = high;
		this.low = low;
		this.inputField = inputField;
	}

	/**
	 * Begin a row.
	 */
	public void beginRow() {
	}

	/**
	 * @return the high
	 */
	public double getHigh() {
		return this.high;
	}

	/**
	 * @return the inputField
	 */
	public InputField getInputField() {
		return this.inputField;
	}

	/**
	 * @return the low
	 */
	public double getLow() {
		return this.low;
	}

	/**
	 * @return True if this item is within range.
	 */
	public boolean isInRange() {
		final double currentValue = this.inputField.getCurrentValue();
		return ((currentValue >= this.low) && (currentValue <= this.high));
	}

}
