package org.encog.ensemble.bagging;

import java.util.ArrayList;
import java.util.Iterator;

import org.encog.ensemble.EnsembleDataSet;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;

public class BaggingDataSet implements EnsembleDataSet {

	private ArrayList<MLDataPair> data;
	private int idealSize;
	private int inputSize;
	
	BaggingDataSet(int inputSize, int idealSize) {
		this.idealSize = idealSize;
		this.inputSize = inputSize;
		data = new ArrayList<MLDataPair>();
	}
	
	@Override
	public int getIdealSize() {
		return idealSize;
	}

	@Override
	public int getInputSize() {
		return inputSize;
	}

	@Override
	public boolean isSupervised() {
		return true;
	}

	@Override
	public long getRecordCount() {
		return data.size();
	}

	@Override
	public void getRecord(long index, MLDataPair pair) {
		final MLDataPair source = this.data.get((int) index);
		pair.setInputArray(source.getInputArray());
		if (pair.getIdealArray() != null) {
			pair.setIdealArray(source.getIdealArray());
		}
	}

	@Override
	public MLDataSet openAdditional() {
		BaggingDataSet copy = new BaggingDataSet(idealSize,inputSize);
		for (MLDataPair line: data) {
			BasicMLDataPair newLine = new BasicMLDataPair(line.getInput(), line.getIdeal());
			copy.add(newLine);
		}
		return copy;
	}

	@Override
	public void add(MLData data1) {
		BasicMLDataPair mlP = new BasicMLDataPair(data1);
		data.add(mlP);
	}

	@Override
	public void add(MLData inputData, MLData idealData) {
		BasicMLDataPair mlP = new BasicMLDataPair(inputData, idealData);
		data.add(mlP);
	}

	@Override
	public void add(MLDataPair inputData) {
		data.add(inputData);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public MLDataPair get(int index) {
		return data.get(index);
	}

	@Override
	public Iterator<MLDataPair> iterator() {
		return data.iterator();
	}

}
