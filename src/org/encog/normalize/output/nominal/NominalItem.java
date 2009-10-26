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

import org.encog.normalize.input.InputField;
import org.encog.persist.annotations.EGAttribute;
import org.encog.persist.annotations.EGReference;

/**
 * A nominal item.
 */
public class NominalItem {

	/**
	 * The low value for the range.
	 */
	@EGAttribute
	private double low;

	/**
	 * The high value for the range.
	 */
	@EGAttribute
	private double high;

	/**
	 * The input field used to verify against the range.
	 */
	@EGReference
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
	public NominalItem(final InputField inputField, final double high,
			final double low) {
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
