package org.encog.ensemble.aggregator;

import java.util.ArrayList;

import org.encog.ensemble.EnsembleAggregator;
import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

public class Averaging implements EnsembleAggregator {

	@Override
	public MLData evaluate(ArrayList<MLData> outputs) {
		int outputSize = outputs.get(0).size();
		BasicMLData acc = new BasicMLData(outputSize);
		for (MLData out: outputs)
		{
			acc = (BasicMLData) acc.plus(out);
		}
		
		acc = (BasicMLData) acc.times(1.0 / outputs.size());
		return 	acc;
		
	}
	
	@Override
	public String toString() {
		return "averaging";
	}

	@Override
	public void train() {
		//This is a no-op in this aggregator
	}
	
	@Override
	public void setTrainingSet(EnsembleDataSet trainingSet) {
		// This is a no-op in this aggregator.
	}

	@Override
	public boolean needsTraining() {
		return false;
	}
}
