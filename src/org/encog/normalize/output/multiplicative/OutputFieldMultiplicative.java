package org.encog.normalize.output.multiplicative;

import org.encog.normalize.NormalizationError;
import org.encog.normalize.input.InputField;
import org.encog.normalize.output.OutputFieldGroup;
import org.encog.normalize.output.OutputFieldGrouped;

public class OutputFieldMultiplicative extends OutputFieldGrouped {

	public OutputFieldMultiplicative(OutputFieldGroup group, final InputField field) {
		super(group, field);
		if( !(group instanceof MultiplicativeGroup) )
			throw new NormalizationError("Must use MultiplicativeGroup with OutputFieldMultiplicative.");
	}

	public double calculate() {
		return this.getSourceField().getCurrentValue()/((MultiplicativeGroup)this.getGroup()).getLength();
	}

}
