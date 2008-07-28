package org.encog.neural.data.bipolar;

import org.encog.matrix.BiPolarUtil;
import org.encog.neural.data.NeuralData;

public class BiPolarNeuralData implements NeuralData {
	private boolean data[];
	
	public BiPolarNeuralData(int size)
	{
		data = new boolean[size];
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

}
