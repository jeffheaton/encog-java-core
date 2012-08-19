package org.encog.ensemble;

import java.util.ArrayList;

import org.encog.ml.data.MLData;

public interface EnsembleAggregator {

	MLData evaluate(ArrayList<MLData> outputs);
	
}
