package org.encog.neural.networks.training.propagation.sgd;

import java.util.Iterator;

import org.encog.EncogError;
import org.encog.mathutil.randomize.generate.GenerateRandom;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

public class StochasticDataSet implements MLDataSet {
	
	private MLDataSet dataset;
	private int[] randomSample;
	private GenerateRandom random;
	
	public StochasticDataSet(MLDataSet theDataset, GenerateRandom theRandom) {
		this.dataset = theDataset;
		this.random = theRandom;
		setBatchSize(500);
	}
	
	public void setBatchSize(int theSize) {
		this.randomSample = new int[theSize];
	}
	
	public void resample() {
		for(int i=0;i<this.randomSample.length;i++) {
			this.randomSample[i] = this.random.nextInt(this.dataset.size());
		}
	}

	@Override
	public Iterator<MLDataPair> iterator() {
		throw new EncogError("Unsupported.");
	}

	@Override
	public int getIdealSize() {
		return this.dataset.getIdealSize();
	}

	@Override
	public int getInputSize() {
		return this.dataset.getInputSize();
	}

	@Override
	public boolean isSupervised() {
		return this.dataset.isSupervised();
	}

	@Override
	public long getRecordCount() {
		return this.randomSample.length;
	}

	@Override
	public void getRecord(long index, MLDataPair pair) {
		this.dataset.getRecord(this.randomSample[(int)index], pair);
	}

	@Override
	public MLDataSet openAdditional() {
		return this;
	}

	@Override
	public void add(MLData data1) {
		throw new EncogError("Unsupported.");
	}

	@Override
	public void add(MLData inputData, MLData idealData) {
		throw new EncogError("Unsupported.");
		
	}

	@Override
	public void add(MLDataPair inputData) {
		throw new EncogError("Unsupported.");
		
	}

	@Override
	public void close() {
		
	}

	@Override
	public int size() {
		return this.randomSample.length;
	}

	@Override
	public MLDataPair get(int index) {
		return this.dataset.get(this.randomSample[index]);
	}

}
