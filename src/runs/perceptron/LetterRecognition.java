package perceptron;


import helpers.LetterMapper;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;


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
		readData("../../data/letter-recognition.data");
		_network = createNetwork();		
		//Important: without proper randomizing the network doesn't train to convergence.
		//(new NguyenWidrowRandomizer(-1,1)).randomize(_network);
		MLTrain train = new ResilientPropagation(_network, _trainingSet);
		//MLTrain train = new ScaledConjugateGradient(network, trainingSet);
		train(train,_trainToError);
	}

}
