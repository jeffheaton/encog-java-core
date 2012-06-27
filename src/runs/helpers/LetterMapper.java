package helpers;

import java.util.ArrayList;

import org.encog.ml.data.MLData;
import org.encog.neural.data.basic.BasicNeuralData;

import perceptron.Mapper;

public class LetterMapper implements Mapper {
	
	private static int _outputs;
	private static double _activationThreshold;
	
	public LetterMapper(int outputs, double activationThreshold)
	{
	  _outputs = outputs;
	  _activationThreshold = activationThreshold;
	}
	
	@Override
	public MLData map(ArrayList<String> data) {
		final BasicNeuralData retVal = new BasicNeuralData(_outputs);
		for (int i = 0; i < _outputs; i++)
			retVal.add(i, 0.0);
		int value = data.get(0).charAt(0) - 'A';
		retVal.setData(value, 1.0);
		return retVal;
	}

	@Override
	public ArrayList<String> unmap(MLData dataSet) {
		char max = '_';
		double maxval = _activationThreshold;
		for(int i=0; i < _outputs; i++)
			if (dataSet.getData(i) > maxval)
			{
				max = (char) ('A' + i);
				maxval = dataSet.getData(i);
			}
		ArrayList<String> retVal = new ArrayList<String>();
		retVal.add("" + max);
		return retVal;
	}

	@Override
	public boolean compare(ArrayList<String> result, ArrayList<String> expected, boolean print) {
		if (print) 
			System.out.println("Exp " + expected.get(0) + " got " + result.get(0));
		return result.get(0).matches(expected.get(0));
	}
	
}