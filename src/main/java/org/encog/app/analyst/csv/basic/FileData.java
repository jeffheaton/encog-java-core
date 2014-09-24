/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.app.analyst.csv.basic;

/**
 * A column that is based off of a column in a CSV file.
 */
public class FileData extends BaseCachedColumn {

	/**
	 * The date.
	 */
	public static final String DATE = "date";

	/**
	 * The time.
	 */
	public static final String TIME = "time";

	/**
	 * The high value.
	 */
	public static final String HIGH = "high";

	/**
	 * The low value.
	 */
	public static final String LOW = "low";

	/**
	 * The open value.
	 */
	public static final String OPEN = "open";

	/**
	 * The close value.
	 */
	public static final String CLOSE = "close";

	/**
	 * The volume.
	 */
	public static final String VOLUME = "volume";

	/**
	 * The index of this field.
	 */
	private int index;

	/**
	 * Construct the object.
	 * 
	 * @param theName
	 *            The name of the object.
	 * @param theIndex
	 *            The index of the field.
	 * @param theInput
	 *            Is this field for input?
	 * @param theOutput
	 *            Is this field for output?
	 */
	public FileData(final String theName, final int theIndex,
			final boolean theInput, final boolean theOutput) {
		super(theName, theInput, theOutput);
		setOutput(theOutput);
		this.index = theIndex;
	}

	/**
	 * @return The index of this field.
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * Set the index of this field.
	 * 
	 * @param theIndex
	 *            The index of this field.
	 */
	public void setIndex(final int theIndex) {
		this.index = theIndex;
	}
}
