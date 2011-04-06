package org.encog.app.analyst.script.normalize;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.script.AnalystClassItem;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.DataField;
import org.encog.app.quant.normalize.ClassItem;
import org.encog.app.quant.normalize.NormalizationAction;

/**
 * This class holds information about the fields that the Encog Analyst will
 * normalize.
 * 
 */
public class AnalystNormalize {

	private AnalystField[] normalizedFields;

	/**
	 * @return the normalizedFields
	 */
	public AnalystField[] getNormalizedFields() {
		return normalizedFields;
	}

	/**
	 * @param normalizedFields
	 *            the normalizedFields to set
	 */
	public void setNormalizedFields(AnalystField[] normalizedFields) {
		this.normalizedFields = normalizedFields;
	}

	public int calculateInputColumns(AnalystField targetField) {
		int result = 0;
		for (AnalystField field : this.normalizedFields) {
			if (field != targetField)
				result += field.getColumnsNeeded();
		}
		return result;
	}

	public int calculateOutputColumns(AnalystField targetField) {
		return targetField.getColumnsNeeded();
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
		for (int i = 0; i < this.normalizedFields.length; i++) {
			if (this.normalizedFields[i].getAction() != NormalizationAction.Ignore)
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
