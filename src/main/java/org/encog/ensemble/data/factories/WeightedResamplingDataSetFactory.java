package org.encog.ensemble.data.factories;

import java.util.Random;

import org.encog.ensemble.data.EnsembleDataPair;
import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.data.MLDataSet;

public class WeightedResamplingDataSetFactory extends EnsembleDataSetFactory {

	public WeightedResamplingDataSetFactory(int dataSetSize) {
		super(dataSetSize);
	}

	MLDataSet originalData;
	
	EnsembleDataPair getCandidate(double weight) {
		double weightSoFar = 0;
		for (int i = 0; i < dataSource.size(); i++) {
			weightSoFar += dataSource.get(i).getSignificance();
			if (weightSoFar > weight)
				return (EnsembleDataPair) dataSource.get(i);
		}
		return (EnsembleDataPair) dataSource.get(dataSource.size());
	}
	
	@Override
	public EnsembleDataSet getNewDataSet() {
		double weightSum = 0;
		for (int i = 0; i < dataSource.size(); i++)
			weightSum += dataSource.get(i).getSignificance();
		Random generator = new Random();
		EnsembleDataSet ds = new EnsembleDataSet(dataSource.getInputSize(), dataSource.getIdealSize());
		for (int i = 0; i < dataSetSize; i++)
		{
			double candidate = generator.nextDouble() * weightSum;
			ds.add(getCandidate(candidate));
		}
		return ds;
	}

}
