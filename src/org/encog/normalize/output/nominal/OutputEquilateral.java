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
 * 
 * Guiver and Klimasauskas (1991)
 */
public class OutputEquilateral extends BasicOutputField {
	private final List<NominalItem> items = new ArrayList<NominalItem>();

	@EGIgnore
	private Equilateral equilateral;
	private int currentValue;
	private double high;
	private double low;

	public OutputEquilateral() {

	}

	public OutputEquilateral(final double high, final double low) {
		this.high = high;
		this.low = low;
	}

	public void addItem(final InputField inputField, final double value) {
		addItem(inputField, value + 0.1, value - 0.1);
	}

	public void addItem(final InputField inputField, final double low,
			final double high) {
		final NominalItem item = new NominalItem(inputField, low, high);
		this.items.add(item);
	}

	public double calculate(final int subfield) {
		return this.equilateral.encode(this.currentValue)[subfield];
	}

	public Equilateral getEquilateral() {
		return this.equilateral;
	}

	public double getHigh() {
		return this.high;
	}

	public double getLow() {
		return this.low;
	}

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
