package org.encog.ensembles.bagging;

import org.encog.ensembles.EnsembleDataSet;
import org.encog.ensembles.EnsembleML;

public class BaggingML implements EnsembleML {

	private EnsembleDataSet dataSet;
	
	@Override
	public void setTrainingSet(EnsembleDataSet dataSet) {
		this.dataSet = dataSet;
	}

	@Override
	public EnsembleDataSet getTrainingSet() {
		return dataSet;
	}

}
