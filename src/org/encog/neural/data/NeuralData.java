package org.encog.neural.data;

public interface NeuralData {

	public void setData(double[] data);
	public void setData(int index,double d);
	public double[] getData();
	public double getData(int index);
	public int size();
	
}
