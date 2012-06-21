package org.encog.ensemble.bagging;

import java.util.Random;

import org.encog.ensemble.EnsembleDataSet;
import org.encog.ensemble.EnsembleDataSetFactory;
import org.encog.ml.data.MLDataSet;

public class BaggingDataSetFactory implements EnsembleDataSetFactory {

	private MLDataSet dataSource;
	private int dataSetSize;
	
	@Override
	public void setInputData(MLDataSet dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public EnsembleDataSet getNewDataSet() {
		Random generator = new Random();
		BaggingDataSet bds = new BaggingDataSet();
		for (int i = 0; i < dataSetSize; i++)
		{
			int candidate = generator.nextInt(dataSource.size());
			bds.add(dataSource.get(candidate));
		}
		return bds;
	}

	@Override
	public boolean hasSource() {
		return (dataSource != null);
	}

	public int getDataSetSize() {
		return dataSetSize;
	}

	public void setDataSetSize(int dataSetSize) {
		this.dataSetSize = dataSetSize;
	}

	public int getInputCount() {
		return this.dataSource.getInputSize();
	}

	public int getOutputCount() {
		return this.dataSource.getIdealSize();
	}
}
