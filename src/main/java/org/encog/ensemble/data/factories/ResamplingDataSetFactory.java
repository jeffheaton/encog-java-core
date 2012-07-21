package org.encog.ensemble.data.factories;

import java.util.Random;

import org.encog.ensemble.data.EnsembleDataSet;


public class ResamplingDataSetFactory extends EnsembleDataSetFactory {
	@Override
	public EnsembleDataSet getNewDataSet() {
		Random generator = new Random();
		EnsembleDataSet bds = new EnsembleDataSet(dataSource.getInputSize(), dataSource.getIdealSize());
		for (int i = 0; i < dataSetSize; i++)
		{
			int candidate = generator.nextInt(dataSource.size());
			bds.add(dataSource.get(candidate));
		}
		return bds;
	}
}
