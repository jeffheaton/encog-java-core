/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.app.analyst.script.normalize;

import java.util.ArrayList;
import java.util.List;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.script.AnalystClassItem;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.DataField;
import org.encog.app.csv.normalize.ClassItem;
import org.encog.app.csv.normalize.NormalizationAction;

/**
 * This class holds information about the fields that the Encog Analyst will
 * normalize.
 * 
 */
public class AnalystNormalize {

	private final List<AnalystField> normalizedFields = new ArrayList<AnalystField>();

	/**
	 * @return the normalizedFields
	 */
	public List<AnalystField> getNormalizedFields() {
		return normalizedFields;
	}

	public int calculateInputColumns() {
		int result = 0;
		for (AnalystField field : this.normalizedFields) {
			if( field.isInput() )
				result += field.getColumnsNeeded();
		}
		return result;
	}

	public int calculateOutputColumns() {
		int result = 0;
		for (AnalystField field : this.normalizedFields) {
			if( field.isOutput() )
				result += field.getColumnsNeeded();
		}
		return result;
	}

	public void init(AnalystScript script) {

		if (this.normalizedFields == null) {
			return;
		}

		for (AnalystField norm : this.normalizedFields) {
			DataField f = script.findDataField(norm.getName());

			if (f == null) {
				throw new AnalystError("Normalize specifies unknown field: "
						+ norm.getName());
			}

			if (norm.getAction() == NormalizationAction.Normalize) {
				norm.setActualHigh(f.getMax());
				norm.setActualLow(f.getMin());
			}

			if (norm.getAction() == NormalizationAction.Equilateral
					|| norm.getAction() == NormalizationAction.OneOf
					|| norm.getAction() == NormalizationAction.SingleField) {

				int index = 0;
				for (AnalystClassItem item : f.getClassMembers()) {
					norm.getClasses().add(
							new ClassItem(item.getName(), index++));
				}
			}
		}
	}

	public int countActiveFields() {
		int result = 0;
		for( AnalystField field : this.normalizedFields ) {
			if (field.getAction() != NormalizationAction.Ignore)
				result++;
		}
		return result;
	}

	/** {@inheritDoc} */
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(": ");
		if (this.normalizedFields != null)
			result.append(this.normalizedFields.toString());
		result.append("]");
		return result.toString();
	}
}
