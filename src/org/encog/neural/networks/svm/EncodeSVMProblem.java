package org.encog.neural.networks.svm;

import java.util.Iterator;

import org.encog.mathutil.libsvm.svm_node;
import org.encog.mathutil.libsvm.svm_problem;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;

public class EncodeSVMProblem {
	
	private EncodeSVMProblem()
	{
		
	}
	
	private static long obtainTrainingLength(NeuralDataSet training)
	{
		if( training instanceof Indexable )
		{
			return ((Indexable)training).getRecordCount();
		}
		
		long result = 0;
		Iterator<NeuralDataPair> itr = training.iterator();
		
		while(itr.hasNext())
			result++;
				
		return result;
	}
	
	public static svm_problem encode(NeuralDataSet training, int outputIndex)
	{
		svm_problem result = new svm_problem();
		
		result.l = (int)obtainTrainingLength(training);
		
		result.y = new double[result.l];
		result.x = new svm_node[result.l][training.getInputSize()];
		
		int elementIndex = 0;
		
		for(NeuralDataPair pair: training)
		{
			NeuralData input = pair.getInput();
			NeuralData output = pair.getIdeal();
			result.x[elementIndex] = new svm_node[input.size()];
			
			for(int i=0;i<input.size();i++)
			{
				result.x[elementIndex][i] = new svm_node();
				result.x[elementIndex][i].index=i+1;
				result.x[elementIndex][i].value=input.getData(i);
			}
			
			result.y[elementIndex] = output.getData(outputIndex);
			
			elementIndex++;
		}
		
		return result;
	}
}
