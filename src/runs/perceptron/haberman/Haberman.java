package perceptron.haberman;

import java.util.ArrayList;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.mathutil.randomize.NguyenWidrowRandomizer;
import org.encog.ml.data.MLData;
import org.encog.ml.train.MLTrain;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import perceptron.Mapper;
import perceptron.Tester;

class IntMapper implements Mapper {
	
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
		char max = '0';
		double maxval = _activationThreshold;
		for(int i=0; i < _outputs; i++)
			if (dataSet.getData(i) > maxval)
			{
				max = (char) ('1' + i);
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

public class Haberman extends Tester {

	public static BasicNetwork createNetwork() {
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,getInputs()));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,100));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),false,getOutputs()));
		network.getStructure().finalizeStructure();
		network.reset();
		return network;
	}
	
	public static void main(String[] args) {
		setInputs(3);
		setOutputs(2);
		setReadInputs(1);
		setReversedInputs();
		setTrainingSetSize(100);
		setMapper(new IntMapper(getOutputs(), 0.3));
		readData("data/haberman.data");
		_network = createNetwork();
		(new NguyenWidrowRandomizer(-1,1)).randomize(_network);
		MLTrain train = new ResilientPropagation(_network, _trainingSet);
		train(train,0.06);
	}

}
