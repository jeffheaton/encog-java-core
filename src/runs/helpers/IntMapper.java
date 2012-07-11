package helpers;

import java.util.ArrayList;

import org.encog.ml.data.MLData;
import org.encog.neural.data.basic.BasicNeuralData;

public class IntMapper implements DataMapper {
	
	private static int _outputs;
	private static double _activationThreshold;
	
	public IntMapper(int outputs, double activationThreshold)
	{
	  _outputs = outputs;
	  _activationThreshold = activationThreshold;
	}
	
	@Override
	public MLData map(ArrayList<String> data) {
		final BasicNeuralData retVal = new BasicNeuralData(_outputs);
		for (int i = 0; i < _outputs; i++)
			retVal.add(i, 0.0);
		int value = Integer.parseInt(data.get(0)) - 1;
		retVal.setData(value, 1.0);
		return retVal;
	}

	@Override
	public ArrayList<String> unmap(MLData dataSet) {
		int max = 0;
		double maxval = _activationThreshold;
		for(int i=0; i < _outputs; i++)
			if (dataSet.getData(i) > maxval)
			{
				max = (i + 1);
				maxval = dataSet.getData(i);
			}
		ArrayList<String> retVal = new ArrayList<String>();
		retVal.add(Integer.toString(max));
		return retVal;
	}

	@Override
	public boolean compare(ArrayList<String> result, ArrayList<String> expected, boolean print) {
		if (print) 
			System.out.println("Exp " + expected.get(0) + " got " + result.get(0));
		return result.get(0).matches(expected.get(0));
	}

	
}