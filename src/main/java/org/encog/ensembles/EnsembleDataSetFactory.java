package org.encog.ensembles;

import org.encog.ml.data.MLDataSet;

public interface EnsembleDataSetFactory {

	public void setInputData(MLDataSet originalData);
	public EnsembleDataSet getNewDataSet();
	public boolean hasSource();
}
