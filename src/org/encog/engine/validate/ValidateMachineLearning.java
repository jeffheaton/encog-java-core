package org.encog.engine.validate;

import org.encog.engine.EngineMachineLearning;

public interface ValidateMachineLearning {
	
	String isValid(EngineMachineLearning network);
	void validate(EngineMachineLearning network);
}
