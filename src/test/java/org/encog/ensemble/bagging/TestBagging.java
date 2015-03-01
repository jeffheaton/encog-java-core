/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ensemble.bagging;

import java.util.ArrayList;
import junit.framework.TestCase;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ensemble.Ensemble.TrainingAborted;
import org.encog.ensemble.EnsembleTrainFactory;
import org.encog.ensemble.aggregator.MajorityVoting;
import org.encog.ensemble.aggregator.WeightedAveraging.WeightMismatchException;
import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ensemble.ml.mlp.factory.MultiLayerPerceptronFactory;
import org.encog.ensemble.training.ResilientPropagationFactory;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.XOR;

public class TestBagging extends TestCase {

	int numSplits = 1;
	int dataSetSize = 100;
	MLDataSet trainingData;

	public void testBagging() {
		trainingData = XOR.createXORDataSet();
		XOR.testXORDataSet(trainingData);
		trainingData = new EnsembleDataSet(trainingData);
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
		try {
			testBagging.train(1E-2,1E-2,(EnsembleDataSet) trainingData);
		} catch (TrainingAborted e) {
			e.printStackTrace();
		}
		for (int j = 0; j < trainingData.size(); j++) {
			MLData input = trainingData.get(j).getInput();
			MLData result;
			try {
				result = testBagging.compute(input);
				MLData should = trainingData.get(j).getIdeal();
				for (int i = 0; i < trainingData.getIdealSize(); i++)
					assertEquals(should.getData()[i],result.getData()[i]);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}