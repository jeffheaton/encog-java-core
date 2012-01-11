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
package org.encog.util.normalize.output;

import org.encog.util.normalize.input.InputField;

/**
 * A direct output field, will simply pass the input value to the output.
 */
public class OutputFieldDirect extends BasicOutputField {

	/**
	 * The source field.
	 */
	private InputField sourceField;

	/**
	 * Construct a direct output field.
	 * @param sourceField The source field to pass directly on.
	 */
	public OutputFieldDirect(final InputField sourceField) {
		this.sourceField = sourceField;
	}

	/**
	 * Default constructor, used for reflection.
	 */
	public OutputFieldDirect() {

	}

	/**
	 * Calculate the value for this field. This will simply be the
	 * value from the input field.
	 * @param subfield Not used, as this output field type does not
	 * support subfields.
	 * @return The calculated value for this field.
	 */
	public double calculate(final int subfield) {
		return this.sourceField.getCurrentValue();
	}

	/**
	 * @return Always returns 1, as subfields are not used.
	 */
	public int getSubfieldCount() {
		return 1;
	}

	/**
	 * Not needed for this sort of output field.
	 */
	public void rowInit() {
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("Direct: ");
		result.append("Source->");
		result.append(this.sourceField.toString());
		return result.toString();
	}

}
