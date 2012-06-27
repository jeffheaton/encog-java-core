package org.encog.ensemble.bagging;

import java.util.ArrayList;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.aggregator.MajorityVoting;
import org.encog.ensemble.ml.mlp.factory.MultiLayerPerceptronFactory;
import org.encog.ensemble.training.backpropagation.BackpropagationFactory;
import org.encog.ensemble.training.backpropagation.ResilientPropagationFactory;
import org.encog.ensemble.training.backpropagation.ScaledConjugateGradientFactory;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.XOR;

import junit.framework.TestCase;

public class TestBagging extends TestCase {

	int numSplits = 1;
	int dataSetSize = 40;
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
		testBagging.train(1E-2,true);
		for (int j = 0; j < trainingData.size(); j++) {
			MLData input = trainingData.get(j).getInput();
			MLData result = testBagging.compute(input);
			MLData should = trainingData.get(j).getIdeal();
			System.out.print("Results: ");
			for (int i = 0; i< input.size(); i++)
				System.out.print(input.getData(i) + ","); 
			System.out.print(" -> ");
			for (int i = 0; i < trainingData.getIdealSize(); i++)
				System.out.println(should.getData(i) + " = " + result.getData(i));
				//assertEquals(should.getData()[i],result.getData()[i]);
		}
	}
}
