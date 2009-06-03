package org.encog.util.benchmark;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.util.randomize.RangeRandomizer;

public class RandomTrainingFactory {
	public static NeuralDataSet generate(int count,int inputCount,int idealCount,double min,double max)
	{
		NeuralDataSet result = new BasicNeuralDataSet();
		for(int i=0;i<count;i++)
		{
			NeuralData inputData = new BasicNeuralData(inputCount);
			
			for(int j=0;j<inputCount;j++)
				inputData.setData(j, RangeRandomizer.randomize(min, max));
			
			NeuralData idealData = new BasicNeuralData(inputCount);
			
			for(int j=0;j<idealCount;j++)
				idealData.setData(j, RangeRandomizer.randomize(min, max));
			
			BasicNeuralDataPair pair = new BasicNeuralDataPair(inputData,idealData);
			result.add(pair);
			
		}
		return result;
	}
}
