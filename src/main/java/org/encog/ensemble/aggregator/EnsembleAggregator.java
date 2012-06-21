package org.encog.ensemble.aggregator;

import java.util.ArrayList;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

public interface EnsembleAggregator {

	MLData evaluate(ArrayList<MLData> outputs);
	
}
