package org.encog.normalize.output.zaxis;

import org.encog.normalize.NormalizationError;
import org.encog.normalize.input.InputField;
import org.encog.normalize.output.OutputFieldGroup;
import org.encog.normalize.output.OutputFieldGrouped;

public class OutputFieldZAxis extends OutputFieldGrouped {

	public OutputFieldZAxis(final OutputFieldGroup group, final InputField field) {
		super(group, field);
		if (!(group instanceof ZAxisGroup)) {
			throw new NormalizationError(
					"Must use ZAxisGroup with OutputFieldZAxis.");
		}
	}

	public double calculate() {
		return (getSourceField().getCurrentValue() * ((ZAxisGroup) getGroup())
				.getMultiplier());
	}

}
