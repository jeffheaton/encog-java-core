package org.encog.normalize.output.zaxis;

import org.encog.normalize.output.BasicOutputFieldGroup;
import org.encog.normalize.output.OutputFieldGrouped;

public class ZAxisGroup extends BasicOutputFieldGroup {

	private double length;
	private double multiplier;

	public double getLength() {
		return this.length;
	}

	public double getMultiplier() {
		return this.multiplier;
	}

	public void rowInit() {
		double value = 0;

		for (final OutputFieldGrouped field : getGroupedFields()) {
			if (!(field instanceof OutputFieldZAxisSynthetic)) {
				if (field.getSourceField() != null) {
					value += (field.getSourceField().getCurrentValue() * field
							.getSourceField().getCurrentValue());
				}
			}
		}
		this.length = Math.sqrt(value);
		this.multiplier = 1.0 / Math.sqrt(getGroupedFields().size());
	}

}
