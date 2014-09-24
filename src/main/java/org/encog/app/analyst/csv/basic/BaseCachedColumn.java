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
 * A basic cached column. Used internally by some of the Encog CSV quant
 * classes. All of the file contents for this column are loaded into memory.
 */
public class BaseCachedColumn {

	/**
	 * The data for this column.
	 */
	private double[] data;

	/**
	 * The name of this column.
	 */
	private String name;

	/**
	 * Is this column used for output?
	 */
	private boolean output;

	/**
	 * Is this column used for input?
	 */
	private boolean input;

	/**
	 * Should this column be ignored.
	 */
	private boolean ignore;

	/**
	 * Construct the cached column.
	 * 
	 * @param theName
	 *            The name of the column.
	 * @param theInput
	 *            Is this column used for input?
	 * @param theOutput
	 *            Is this column used for output?
	 */
	public BaseCachedColumn(final String theName, final boolean theInput,
			final boolean theOutput) {
		this.name = theName;
		this.input = theInput;
		this.output = theOutput;
		this.ignore = false;
	}

	/**
	 * Allocate enough space for this column.
	 * 
	 * @param length
	 *            The length of this column.
	 */
	public void allocate(final int length) {
		this.data = new double[length];
	}

	/**
	 * @return The data for this column.
	 */
	public double[] getData() {
		return this.data;
	}

	/**
	 * @return The name of this column
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return Is this column ignored?
	 */
	public boolean isIgnore() {
		return this.ignore;
	}

	/**
	 * @return Is this column used for input?
	 */
	public boolean isInput() {
		return this.input;
	}

	/**
	 * @return Is this column used for output?
	 */
	public boolean isOutput() {
		return this.output;
	}

	/**
	 * Set if this column is to be ignored?
	 * 
	 * @param theIgnore
	 *            True, if this column is to be ignored.
	 */
	public void setIgnore(final boolean theIgnore) {
		this.ignore = theIgnore;
	}

	/**
	 * Set if this column is used for input.
	 * 
	 * @param theIgnore
	 *            Is this column used for input.
	 */
	public void setInput(final boolean theIgnore) {
		this.input = theIgnore;
	}

	/**
	 * Set the name of this column.
	 * 
	 * @param theName
	 *            The name of this column.
	 */
	public void setName(final String theName) {
		this.name = theName;
	}

	/**
	 * Set if this column is used for output.
	 * 
	 * @param theOutput
	 *            Is this column used for output.
	 */
	public void setOutput(final boolean theOutput) {
		this.output = theOutput;
	}

}
