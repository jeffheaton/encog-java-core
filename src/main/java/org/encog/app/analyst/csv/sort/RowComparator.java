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
package org.encog.app.analyst.csv.sort;

import java.util.Comparator;

import org.encog.app.analyst.csv.basic.LoadedRow;
import org.encog.app.quant.QuantError;

/**
 * Used to compare two LoadedRow objects.
 *
 */
public class RowComparator implements Comparator<LoadedRow> {

	/**
	 * The owner object.
	 */
	private final SortCSV sort;

	/**
	 * Construct the object.
	 * @param owner The owner.
	 */
	public RowComparator(final SortCSV owner) {
		this.sort = owner;
	}

	/**
	 * Compare two LoadedRow objects.
	 * 
	 * @param x
	 *            The first object to compare.
	 * @param y
	 *            The second object to compare.
	 * @return 0 if the same, <0 x is less, >0 y is less.
	 */
	@Override
	public final int compare(final LoadedRow x, final LoadedRow y) {
		for (final SortedField t : this.sort.getSortOrder()) {
			final int index = t.getIndex();
			final String xStr = x.getData()[index];
			final String yStr = y.getData()[index];

			switch (t.getSortType()) {
			case SortDecimal:
				final double xDouble = this.sort.getFormat().parse(xStr);
				final double yDouble = this.sort.getFormat().parse(yStr);
				final int c = Double.compare(xDouble, yDouble);
				if (c != 0) {
					return c;
				}
				break;
			case SortInteger:
				final int xInteger = Integer.parseInt(xStr);
				final int yInteger = Integer.parseInt(yStr);
				final int c2 = xInteger - yInteger;
				if (c2 != 0) {
					return c2;
				}
				break;
			case SortString:
				final int c3 = xStr.compareTo(yStr);
				if (c3 != 0) {
					return c3;
				}
				break;
			default:
				throw new QuantError("Unknown sort method: " 
						+ t.getSortType());
			}
		}

		// failing all of this, they are equal
		return 0;
	}

}
