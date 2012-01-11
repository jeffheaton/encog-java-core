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
package org.encog.util.arrayutil;

/**
 * A class item.
 */
public class ClassItem {
	/**
	 * The name of the class.
	 */
	private String name;

	/**
	 * The index of the class.
	 */
	private int index;

	/**
	 * Construct the object.
	 * 
	 * @param theName
	 *            The name of the class.
	 * @param theIndex
	 *            The index of the class.
	 */
	public ClassItem(final String theName, final int theIndex) {
		this.name = theName;
		this.index = theIndex;
	}

	/**
	 * @return The index of the class.
	 */
	public final int getIndex() {
		return this.index;
	}

	/**
	 * @return The name of the class.
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Set the index of the class.
	 * 
	 * @param theIndex
	 *            The index of the class.
	 */
	public final void setIndex(final int theIndex) {
		this.index = theIndex;
	}

	/**
	 * Set the name of the class.
	 * 
	 * @param theName
	 *            The name of the class.
	 */
	public final void setName(final String theName) {
		this.name = theName;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" name=");
		result.append(this.name);
		result.append(", index=");
		result.append(this.index);

		result.append("]");
		return result.toString();
	}

}
