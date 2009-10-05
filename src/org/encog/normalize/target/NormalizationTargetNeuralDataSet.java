package org.encog.normalize.target;

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;

public class NormalizationTargetNeuralDataSet implements NormalizationTarget {

	private int inputCount;
	private int idealCount;
	private NeuralDataSet dataset;
	
	public NormalizationTargetNeuralDataSet(int inputCount,int idealCount )
	{
		this.inputCount = inputCount;
		this.idealCount = idealCount;
		this.dataset = new BasicNeuralDataSet();
	}
	
	public NormalizationTargetNeuralDataSet(NeuralDataSet dataset)
	{
		this.dataset = dataset;
		this.inputCount = this.dataset.getInputSize();
		this.idealCount = this.dataset.getIdealSize();
	}
	
	public void close() {
	}

	public void open() {
	}

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
