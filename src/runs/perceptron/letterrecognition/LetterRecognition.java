package perceptron.letterrecognition;

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

class LetterMapper implements Mapper {
	
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

public class LetterRecognition extends Tester {
	
	private static double _activationThreshold = 0.3;
	private static double _trainToError = 0.1;

	public static BasicNetwork createNetwork() {
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null,true,getInputs()));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,300));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),false,getOutputs()));
		network.getStructure().finalizeStructure();
		network.reset();
		return network;
	}

	public static void main(String[] args) {
		setInputs(16);
		setOutputs('Z' - 'A' + 1);
		setReadInputs(1);
		setTrainingSetSize(2000);
		setMapper(new LetterMapper(getOutputs(), _activationThreshold));
		readData("data/letter-recognition.data");
		_network = createNetwork();		
		//Important: without proper randomizing the network doesn't train to convergence.
		(new NguyenWidrowRandomizer(-1,1)).randomize(_network);
		MLTrain train = new ResilientPropagation(_network, _trainingSet);
		//MLTrain train = new ScaledConjugateGradient(network, trainingSet);
		train(train,_trainToError);
	}

}
