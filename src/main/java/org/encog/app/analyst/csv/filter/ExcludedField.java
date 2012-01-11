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
package org.encog.app.analyst.csv.filter;

/**
 * Used internally to track excluded fields from the FilterCSV.
 */
public class ExcludedField {

	/**
	 * The field number.
	 */
	private int fieldNumber;

	/**
	 * The field value to filter on.
	 */
	private String fieldValue;

	/**
	 * Construct the object.
	 * 
	 * @param theFieldNumber
	 *            The field number.
	 * @param theFieldValue
	 *            The field value to filter on.
	 */
	public ExcludedField(final int theFieldNumber, final String theFieldValue) {
		this.fieldNumber = theFieldNumber;
		this.fieldValue = theFieldValue;
	}

	/**
	 * @return the fieldNumber
	 */
	public final int getFieldNumber() {
		return this.fieldNumber;
	}

	/**
	 * @return the fieldValue
	 */
	public final String getFieldValue() {
		return this.fieldValue;
	}

	/**
	 * @param theFieldNumber
	 *            the fieldNumber to set
	 */
	public final void setFieldNumber(final int theFieldNumber) {
		this.fieldNumber = theFieldNumber;
	}

	/**
	 * @param theFieldValue
	 *            the fieldValue to set
	 */
	public final void setFieldValue(final String theFieldValue) {
		this.fieldValue = theFieldValue;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" fieldNumber=");
		result.append(this.fieldNumber);
		result.append(", value=");
		result.append(this.fieldValue);

		result.append("]");
		return result.toString();
	}

}
