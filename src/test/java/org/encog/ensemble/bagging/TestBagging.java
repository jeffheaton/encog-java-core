package org.encog.ensemble.bagging;

import java.util.ArrayList;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.ml.mlp.factory.MultiLayerPerceptronFactory;
import org.encog.ensemble.training.backpropagation.BackpropagationFactory;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.factory.MLMethodFactory;

import junit.framework.TestCase;

public class TestBagging extends TestCase {

	public void testBagging()
	{
		int numSplits = 10;
		int dataSetSize = 2;
		MLDataSet trainingData = new BasicMLDataSet(
			new double[][] {{0.0,1.0},
							{1.0,0.0},
							{0.0,0.0},
							{1.0,1.0}
						},
			new double[][] {{1.0},
							{1.0},
							{0.0},
							{0.0}
						});
		EnsembleTrainFactory trainingStrategy = new BackpropagationFactory();
		MultiLayerPerceptronFactory mlpFactory = new MultiLayerPerceptronFactory();
		ArrayList<Integer> middleLayers = new ArrayList<Integer>();
		middleLayers.add(3);
		middleLayers.add(3);
		mlpFactory.setParameters(middleLayers, new ActivationSigmoid());
		Bagging testBagging = new Bagging(numSplits, dataSetSize, mlpFactory, trainingStrategy);
		testBagging.setTrainingData(trainingData);
	}
}
