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

/**
 * Specifies how a field is to be sorted by SortCSV.
 */
public class SortedField {

	/**
	 * The index of the field.
	 */
	private int index;

	/**
	 * True, if the field is to be sorted ascending.
	 */
	private boolean ascending;

	/**
	 * The type of sort.
	 */
	private SortType sortType;

	/**
	 * Construct the object.
	 * 
	 * @param theIndexindex
	 *            The index of the sorted field.
	 * @param t
	 *            The type of sort, the type of object.
	 * @param theAscending
	 *            True, if this is an ascending sort.
	 */
	public SortedField(final int theIndexindex, final SortType t,
			final boolean theAscending) {
		this.index = theIndexindex;
		this.ascending = theAscending;
		this.sortType = t;
	}

	/**
	 * @return the index
	 */
	public final int getIndex() {
		return this.index;
	}

	/**
	 * @return the sortType
	 */
	public final SortType getSortType() {
		return this.sortType;
	}

	/**
	 * @return the ascending
	 */
	public final boolean isAscending() {
		return this.ascending;
	}

	/**
	 * @param theAscending
	 *            the ascending to set
	 */
	public final void setAscending(final boolean theAscending) {
		this.ascending = theAscending;
	}

	/**
	 * @param theIndex
	 *            the index to set
	 */
	public final void setIndex(final int theIndex) {
		this.index = theIndex;
	}

	/**
	 * @param theSortType
	 *            the sortType to set
	 */
	public final void setSortType(final SortType theSortType) {
		this.sortType = theSortType;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" index=");
		result.append(this.index);
		result.append(", type=");
		result.append(this.sortType);

		result.append("]");
		return result.toString();
	}

}
