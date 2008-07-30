package org.encog.neural.data.bipolar;

import org.encog.matrix.BiPolarUtil;
import org.encog.neural.data.NeuralData;

public class BiPolarNeuralData implements NeuralData {
	private boolean data[];
	
	public BiPolarNeuralData(int size)
	{
		data = new boolean[size];
	}
	
	public BiPolarNeuralData(boolean[] d)
	{
		data = new boolean[d.length];
		System.arraycopy(d, 0, data, 0, d.length);
	}
	
	public double[] getData() {
		return BiPolarUtil.bipolar2double(data);
	}

	public double getData(int index) {
		return BiPolarUtil.bipolar2double(data[index]);
	}

	public void setData(double[] data) {
		this.data = BiPolarUtil.double2bipolar(data);
		
	}

	public void setData(int index, double d) {
		this.data[index] = BiPolarUtil.double2bipolar(d);		
	}

	public int size() {
		return this.data.length;
	}
	
	
	public void setData(int index,boolean value)
	{
		data[index] = value;
	}

	public boolean getBoolean(int i) {
		return data[i];
	}

}
