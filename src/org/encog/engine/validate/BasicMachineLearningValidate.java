package org.encog.engine.validate;

import org.encog.engine.EngineMachineLearning;

public abstract class BasicMachineLearningValidate implements ValidateMachineLearning {

	@Override
	public void validate(EngineMachineLearning network) {
		String msg = isValid(network);
		if( msg!=null )
			throw new ValidateMachineLearningError(msg);
	}

}
