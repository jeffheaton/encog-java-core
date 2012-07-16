package org.encog.ensemble.adaboost;

import java.util.ArrayList;
import java.util.TreeMap;

import org.encog.ensemble.EnsembleDataSet;
import org.encog.ensemble.EnsembleDataSetFactory;
import org.encog.ml.data.MLDataSet;

public class AdaBoostDataSetFactory implements EnsembleDataSetFactory {

	MLDataSet originalData;
	
	@Override
	public void setInputData(MLDataSet originalData) {
		this.originalData = originalData;
	}

	@Override
	public EnsembleDataSet getNewDataSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSource() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MLDataSet getInputData() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setWeights(ArrayList<Double> d) {
		// TODO Auto-generated method stub
		
	}

}
