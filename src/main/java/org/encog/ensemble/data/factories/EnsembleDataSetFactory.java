package org.encog.ensemble.data.factories;

import java.util.ArrayList;

import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.data.MLDataSet;

public abstract class EnsembleDataSetFactory {

	protected MLDataSet dataSource = null;
	protected int dataSetSize;

	public EnsembleDataSetFactory(int dataSetSize) {
		setDataSetSize(dataSetSize);
	}
	
	public void setInputData(MLDataSet dataSource) {
		this.dataSource = dataSource;
	}
	
	abstract public EnsembleDataSet getNewDataSet();
	
	public boolean hasSource() {
		return (dataSource != null);
	}
	
	public MLDataSet getInputData() {
		return this.dataSource;
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

	public void setSignificance(ArrayList<Double> D) {
		for (int i = 0; i < dataSource.size(); i++)
			dataSource.get(i).setSignificance(D.get(i));
	}
	
	public ArrayList<Double> getSignificance() {
		ArrayList<Double> res = new ArrayList<Double>();
		for (int i = 0; i < dataSource.size(); i++)
			res.add(dataSource.get(i).getSignificance());
		return res;
	}
	
}
