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

public class OutputOneOf extends BasicOutputField {

	private final List<NominalItem> items = new ArrayList<NominalItem>();
	private double trueValue;
	private double falseValue;

	public OutputOneOf() {

	}

	public OutputOneOf(final double trueValue, final double falseValue) {
		this.trueValue = trueValue;
		this.falseValue = falseValue;
	}

	public void addItem(final InputField inputField, final double value) {
		addItem(inputField, value - 0.5, value + 0.5);
	}

	public void addItem(final InputField inputField, final double low,
			final double high) {
		final NominalItem item = new NominalItem(inputField, low, high);
		this.items.add(item);
	}

	public double calculate(final int subfield) {
		final NominalItem item = this.items.get(subfield);
		return item.isInRange() ? this.trueValue : this.falseValue;
	}

	public double getFalseValue() {
		return this.falseValue;
	}

	public int getSubfieldCount() {
		return this.items.size();
	}

	public double getTrueValue() {
		return this.trueValue;
	}

	/**
	 * Not needed for this sort of output field.
	 */
	public void rowInit() {
	}

}
