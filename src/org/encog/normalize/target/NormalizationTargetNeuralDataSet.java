package org.encog.normalize.target;

import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public class NormalizationTargetNeuralDataSet implements NormalizationTarget {

	private int inputCount;
	private int idealCount;
	private BasicNeuralDataSet dataset;
	
	public NormalizationTargetNeuralDataSet(int inputCount,int idealCount )
	{
		this.inputCount = inputCount;
		this.idealCount = idealCount;
		this.dataset = new BasicNeuralDataSet();
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(double[] data, int inputCount) {
		
		if( this.idealCount==0 )
		{
			BasicNeuralData inputData = new BasicNeuralData(data);
			this.dataset.add(inputData);
		}
		else
		{
			BasicNeuralData inputData = new BasicNeuralData(this.inputCount);
			BasicNeuralData idealData = new BasicNeuralData(this.idealCount);
			
			int index =0;
			for(int i=0;i<this.inputCount;i++)
			{
				inputData.setData(i,data[index++]);
			}
			
			for(int i=0;i<this.idealCount;i++)
			{
				idealData.setData(i,data[index++]);
			}
			
			this.dataset.add(inputData,idealData);
		}
			
		
	}

}
