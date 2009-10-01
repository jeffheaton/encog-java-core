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
		// TODO Auto-generated method stub
		return 0;
	}

}
