package org.encog.ensemble;

import java.util.Random;

import org.encog.ml.data.MLDataSet;

public class WeightedResamplingDataSetFactory extends EnsembleDataSetFactory {

	MLDataSet originalData;
	
	EnsembleDataPair getCandidate(double weight) {
		double weightSoFar = 0;
		for (int i = 0; i < dataSetSize; i++) {
			weightSoFar += dataSource.get(i).getSignificance();
			if (weightSoFar > weight)
				return (EnsembleDataPair) dataSource.get(i);
		}
		return (EnsembleDataPair) dataSource.get(dataSetSize);
	}
	
	@Override
	public EnsembleDataSet getNewDataSet() {
		double weightSum = 0;
		for (int i = 0; i < dataSetSize; i++)
			weightSum += dataSource.get(i).getSignificance();
		Random generator = new Random();
		EnsembleDataSet bds = new EnsembleDataSet(dataSource.getInputSize(), dataSource.getIdealSize());
		for (int i = 0; i < dataSetSize; i++)
		{
			double candidate = generator.nextDouble() * weightSum;
			bds.add(getCandidate(candidate));
		}
		return bds;
	}

}
