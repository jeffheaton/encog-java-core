package org.encog.ensemble.data.factories;

import org.encog.ensemble.data.EnsembleDataSet;

public class WrappingNonResamplingDataSetFactory extends EnsembleDataSetFactory {

	//NOTE: dataSetSize here is used as the number of datasets, rather than the number of data instances


	private int currentPosition = 0;

	public WrappingNonResamplingDataSetFactory(int dataSetSize) {
		super(dataSetSize);
	}

	@Override
	public EnsembleDataSet getNewDataSet() {
		EnsembleDataSet ds = new EnsembleDataSet(dataSource.getInputSize(), dataSource.getIdealSize());
		for (int i = currentPosition; i < currentPosition + dataSource.size() / dataSetSize; i++)
		{
			ds.add(dataSource.get(i % this.dataSource.size()));
		}
		return ds;
	}
}
