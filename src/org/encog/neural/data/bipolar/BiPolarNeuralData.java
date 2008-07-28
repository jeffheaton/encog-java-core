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
	
	@Override
	public double[] getData() {
		return BiPolarUtil.bipolar2double(data);
	}

	@Override
	public double getData(int index) {
		return BiPolarUtil.bipolar2double(data[index]);
	}

	@Override
	public void setData(double[] data) {
		this.data = BiPolarUtil.double2bipolar(data);
		
	}

	@Override
	public void setData(int index, double d) {
		this.data[index] = BiPolarUtil.double2bipolar(d);		
	}

	@Override
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
