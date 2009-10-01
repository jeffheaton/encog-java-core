package org.encog.normalize.output.zaxis;

import org.encog.normalize.NormalizationError;
import org.encog.normalize.input.InputField;
import org.encog.normalize.output.OutputFieldGroup;
import org.encog.normalize.output.OutputFieldGrouped;
import org.encog.normalize.output.multiplicative.MultiplicativeGroup;

public class OutputFieldZAxis extends OutputFieldGrouped {

	public OutputFieldZAxis(OutputFieldGroup group, final InputField field) {
		super(group,field);
		if( !(group instanceof ZAxisGroup ) )
			throw new NormalizationError("Must use ZAxisGroup with OutputFieldZAxis.");
	}

	public double calculate() {
		return(this.getSourceField().getCurrentValue()*((ZAxisGroup)this.getGroup()).getMultiplier());
	}

}
