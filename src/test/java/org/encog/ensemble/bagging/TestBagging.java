package org.encog.ensemble.bagging;

import java.util.ArrayList;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.aggregator.MajorityVoting;
import org.encog.ensemble.ml.mlp.factory.MultiLayerPerceptronFactory;
import org.encog.ensemble.training.backpropagation.ResilientPropagationFactory;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.XOR;

import junit.framework.TestCase;

public class TestBagging extends TestCase {

	int numSplits = 1;
	int dataSetSize = 100;
	MLDataSet trainingData;
	
	
	public void testBagging() {
		trainingData = XOR.createXORDataSet();
		XOR.testXORDataSet(trainingData);
		assertEquals(1,trainingData.getIdealSize());
		assertEquals(2,trainingData.getInputSize());
		EnsembleTrainFactory trainingStrategy = new ResilientPropagationFactory();
		MultiLayerPerceptronFactory mlpFactory = new MultiLayerPerceptronFactory();
		ArrayList<Integer> middleLayers = new ArrayList<Integer>();
		middleLayers.add(4);
		mlpFactory.setParameters(middleLayers, new ActivationSigmoid());
		MajorityVoting mv = new MajorityVoting();
		Bagging testBagging = new Bagging(numSplits, dataSetSize, mlpFactory, trainingStrategy, mv);
		testBagging.setTrainingData(trainingData);
		testBagging.train(1E-2);
		for (int j = 0; j < trainingData.size(); j++) {
			MLData input = trainingData.get(j).getInput();
			MLData result = testBagging.compute(input);
			MLData should = trainingData.get(j).getIdeal();
			for (int i = 0; i < trainingData.getIdealSize(); i++)
				assertEquals(should.getData()[i],result.getData()[i]);
		}
	}
}
