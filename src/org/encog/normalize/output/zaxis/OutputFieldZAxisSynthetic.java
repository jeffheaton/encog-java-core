package org.encog.normalize.output.zaxis;

import org.encog.normalize.NormalizationError;
import org.encog.normalize.output.OutputFieldGroup;
import org.encog.normalize.output.OutputFieldGrouped;

public class OutputFieldZAxisSynthetic extends OutputFieldGrouped {

	public OutputFieldZAxisSynthetic(OutputFieldGroup group) {
		super(group,null);
		if( !(group instanceof ZAxisGroup ) )
			throw new NormalizationError("Must use ZAxisGroup with OutputFieldZAxisSynthetic.");
	}

	public double calculate() {
		double l = ((ZAxisGroup)this.getGroup()).getLength();
		double f = ((ZAxisGroup)this.getGroup()).getMultiplier();
		double n = this.getGroup().getGroupedFields().size();
		double result = f*Math.sqrt(n-(l*l));
		if( Double.isInfinite(result) || Double.isNaN(result))
			return 0;
		else
			return result;
	}

}
