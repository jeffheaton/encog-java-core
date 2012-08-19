package org.encog.ensemble.aggregator;

import java.util.ArrayList;

import org.encog.ensemble.EnsembleAggregator;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

public class MajorityVoting implements EnsembleAggregator {
	
	public MLData evaluate(ArrayList<MLData> outputs, double threshold, double lowValue, double highValue) {
		int outputSize = outputs.get(0).size();
		BasicMLData acc = new BasicMLData(outputSize);
		for (MLData out: outputs)
		{
			BasicMLData thresholdedOut = (BasicMLData) ((BasicMLData) out).threshold(threshold, lowValue, highValue);
			acc = (BasicMLData) acc.plus(thresholdedOut);
		}
		
		acc = (BasicMLData) acc.times(1.0 / outputs.size());
		return acc.threshold(threshold, lowValue, highValue);
		
	}
	
	@Override
	public MLData evaluate(ArrayList<MLData> outputs) {
		return evaluate(outputs, 0.5, 0.0, 1.0);
	}
	
	@Override
	public String toString() {
		return "majorityvoting";
	}

}
