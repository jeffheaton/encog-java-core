package org.encog.ensemble.data.factories;

import java.util.Random;

import org.encog.ensemble.data.EnsembleDataSet;

public class ResamplingDataSetFactory extends EnsembleDataSetFactory {

	public ResamplingDataSetFactory(int dataSetSize) {
		super(dataSetSize);
	}

	@Override
	public EnsembleDataSet getNewDataSet() {
		Random generator = new Random();
		EnsembleDataSet ds = new EnsembleDataSet(dataSource.getInputSize(), dataSource.getIdealSize());
		for (int i = 0; i < dataSetSize; i++)
		{
			int candidate = generator.nextInt(dataSource.size());
			ds.add(dataSource.get(candidate));
		}
		return ds;
	}
}
