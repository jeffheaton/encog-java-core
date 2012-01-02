package org.encog.ml.data;

import org.encog.util.kmeans.Centroid;

public class MLDataBreak implements MLData {

	@Override
	public void add(int index, double value) {
		
	}

	@Override
	public void clear() {
		
	}

	@Override
	public double[] getData() {
		return new double[0];
	}

	@Override
	public double getData(int index) {
		return 0;
	}

	@Override
	public void setData(double[] data) {
		
	}

	@Override
	public void setData(int index, double d) {
		
	}

	@Override
	public int size() {
		return 0;
	}
	
	public MLDataBreak clone() {
		return new MLDataBreak();
	}

	@Override
	public Centroid<MLData> createCentroid() {
		return null;
	}

}
