package org.encog.ensemble.aggregator;

import java.util.ArrayList;

import org.encog.ml.data.MLData;

public interface EnsembleAggregator {

	MLData evaluate(ArrayList<MLData> outputs);
	
}
