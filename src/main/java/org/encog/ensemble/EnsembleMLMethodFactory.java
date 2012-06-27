package org.encog.ensemble;

import org.encog.ml.MLMethod;

public interface EnsembleMLMethodFactory {

	public MLMethod createML(int inputs, int outputs);
	
}
