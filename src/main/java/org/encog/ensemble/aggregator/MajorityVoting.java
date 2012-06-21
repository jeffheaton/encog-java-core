package org.encog.ensemble.aggregator;

import java.util.ArrayList;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

public class MajorityVoting implements EnsembleAggregator {

	@Override
	public MLData evaluate(ArrayList<MLData> outputs) {
		int outputSize = outputs.get(0).size();
		BasicMLData acc = new BasicMLData(outputSize);
		for (MLData out: outputs)
		{
			BasicMLData thresholdedOut = (BasicMLData) ((BasicMLData) out).threshold(0.5, 0.0, 1.0);
			acc = (BasicMLData) acc.plus(thresholdedOut);
		}
		
		acc = (BasicMLData) acc.times(1.0 / outputs.size());
		return 	acc.threshold(0.5, 0.0, 1.0);
		
	}

}
