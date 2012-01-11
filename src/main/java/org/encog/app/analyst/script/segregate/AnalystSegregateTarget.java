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
package org.encog.app.analyst.script.segregate;

/**
 * This class specifies a target for the segregation process.
 * 
 */
public class AnalystSegregateTarget {
	
	/**
	 * The file target.
	 */
	private String file;
	
	/**
	 * The percent.
	 */
	private int percent;

	/**
	 * Construct the segregation target.
	 * @param theFile The file.
	 * @param thePercent The percent.
	 */
	public AnalystSegregateTarget(final String theFile, final int thePercent) {
		super();
		this.file = theFile;
		this.percent = thePercent;
	}

	/**
	 * @return the file
	 */
	public final String getFile() {
		return this.file;
	}

	/**
	 * @return the percent
	 */
	public final int getPercent() {
		return this.percent;
	}

	/**
	 * @param theFile
	 *            the file to set
	 */
	public final void setFile(final String theFile) {
		this.file = theFile;
	}

	/**
	 * @param thePercent
	 *            the percent to set
	 */
	public final void setPercent(final int thePercent) {
		this.percent = thePercent;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" file=");
		result.append(this.file.toString());
		result.append(", percent=");
		result.append(this.file);
		result.append("]");
		return result.toString();
	}

}
