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
package org.encog.app.analyst.script;

/**
 * Holds a class item for the script. Some columns in a CSV are classes. This
 * object holds the possible class types.
 * 
 */
public class AnalystClassItem implements Comparable<AnalystClassItem> {

	/**
	 * THe code for the class item.
	 */
	private String code;
	
	/**
	 * The name for the class item.
	 */
	private String name;
	
	/**
	 * THe count.
	 */
	private int count;

	/**
	 * Construct a class item.
	 * @param theCode The code, this is how it is in the CSV.
	 * @param theName The name, a more meaningful name than code.  
	 * Can be the same as code, if desired.
	 * @param theCount The count.
	 */
	public AnalystClassItem(final String theCode, final String theName,
			final int theCount) {
		super();
		this.code = theCode;
		this.name = theName;
		this.count = theCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(final AnalystClassItem o) {
		return this.code.compareTo(o.getCode());
	}

	/**
	 * @return the code
	 */
	public final String getCode() {
		return this.code;
	}

	/**
	 * @return The count.
	 */
	public final int getCount() {
		return this.count;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Increase the count.
	 */
	public final void increaseCount() {
		this.count++;
	}

	/**
	 * @param theCode
	 *            the code to set
	 */
	public final void setCode(final String theCode) {
		this.code = theCode;
	}

	/**
	 * @param theName
	 *            the name to set
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
		result.append(", code=");
		result.append(this.code);
		result.append("]");
		return result.toString();
	}

}
