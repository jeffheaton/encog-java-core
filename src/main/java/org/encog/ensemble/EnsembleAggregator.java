package org.encog.ensemble;

import java.util.ArrayList;

import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.data.MLData;

public interface EnsembleAggregator {

	MLData evaluate(ArrayList<MLData> outputs);
	
	public boolean needsTraining();
	void setTrainingSet(EnsembleDataSet trainingSet);
	void train();
	
}
