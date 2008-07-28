package org.encog.neural.data.basic;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;

public class BasicNeuralDataSet implements NeuralDataSet {
	private List<NeuralData> input = new ArrayList<NeuralData>();
	private List<NeuralData> ideal = new ArrayList<NeuralData>();
	
	public BasicNeuralDataSet()
	{
		
	}
	
	public BasicNeuralDataSet(double input[][],double ideal[][])
	{
		for(int i=0;i<input.length;i++)
		{
			BasicNeuralData inputData = new BasicNeuralData(input[i]);
			BasicNeuralData idealData = new BasicNeuralData(ideal[i]);
			this.add(inputData,idealData);
		}
	}
	
	public void add(NeuralData inputData,NeuralData idealData)
	{
		this.input.add(inputData);
		this.ideal.add(idealData);
	}
	
	public void add(NeuralData inputData)
	{
		this.input.add(inputData);
	}
	
	public NeuralData getInput(int index)
	{
		return input.get(index);
	}
	
	public NeuralData getIdeal(int index)
	{
		return ideal.get(index);
	}

	@Override
	public int size() {
		return input.size();
	}
}
