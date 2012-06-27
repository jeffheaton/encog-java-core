package perceptron.haberman;


import helpers.IntMapper;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import perceptron.Tester;

public class Haberman extends Tester {

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
		setInputs(3);
		setOutputs(2);
		setReadInputs(1);
		setReversedInputs();
		setTrainingSetSize(100);
		setMapper(new IntMapper(getOutputs(), 0.3));
		readData("../../data/haberman.data");
		_network = createNetwork();
		//(new NguyenWidrowRandomizer(-1,1)).randomize(_network);
		MLTrain train = new ResilientPropagation(_network, _trainingSet);
		train(train,0.06);
	}

}
