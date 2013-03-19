package org.encog.ensemble.aggregator;

import java.util.ArrayList;

import org.encog.ensemble.EnsembleAggregator;
import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

public class MajorityVoting implements EnsembleAggregator {

	public MLData evaluate(ArrayList<MLData> outputs, double threshold, double lowValue, double highValue) {
		int outputSize = outputs.get(0).size();
		BasicMLData acc = new BasicMLData(outputSize);
		for (MLData out: outputs)
		{
			MLData thresholdedOut = threshold(out, threshold, lowValue, highValue);
			acc = (BasicMLData) acc.plus(thresholdedOut);
		}

		acc = (BasicMLData) acc.times(1.0 / outputs.size());
		return threshold(acc, threshold, lowValue, highValue);
	}
	
	private MLData threshold(MLData d, double threshold, double lowValue, double highValue) {
		MLData result = new BasicMLData(d.size());
		for(int i=0;i<d.size();i++) {
			if( d.getData(i)>=threshold ) {
				result.setData(i, highValue);
			} else {
				result.setData(i, lowValue);
			}
		}
		return result;
	}

	@Override
	public MLData evaluate(ArrayList<MLData> outputs) {
		return evaluate(outputs, 0.5, 0.0, 1.0);
	}

	@Override
	public String getLabel() {
		return "majorityvoting";
	}

	@Override
	public void train() {
		// This is a no-op in this aggregator.
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
