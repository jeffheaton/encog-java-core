package org.encog.normalize.output.zaxis;

import org.encog.normalize.NormalizationError;
import org.encog.normalize.output.OutputFieldGroup;
import org.encog.normalize.output.OutputFieldGrouped;

public class OutputFieldZAxisSynthetic extends OutputFieldGrouped {

	public OutputFieldZAxisSynthetic(final OutputFieldGroup group) {
		super(group, null);
		if (!(group instanceof ZAxisGroup)) {
			throw new NormalizationError(
					"Must use ZAxisGroup with OutputFieldZAxisSynthetic.");
		}
	}

	public double calculate() {
		final double l = ((ZAxisGroup) getGroup()).getLength();
		final double f = ((ZAxisGroup) getGroup()).getMultiplier();
		final double n = getGroup().getGroupedFields().size();
		final double result = f * Math.sqrt(n - (l * l));
		if (Double.isInfinite(result) || Double.isNaN(result)) {
			return 0;
		} else {
			return result;
		}
	}

}
