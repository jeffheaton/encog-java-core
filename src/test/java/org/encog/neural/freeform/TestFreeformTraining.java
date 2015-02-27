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
package org.encog.neural.freeform;

import junit.framework.TestCase;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.MethodFactory;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.genetic.MLMethodGeneticAlgorithm;
import org.encog.ml.train.MLTrain;
import org.encog.neural.freeform.training.FreeformBackPropagation;
import org.encog.neural.freeform.training.FreeformResilientPropagation;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.junit.Test;

public class TestFreeformTraining extends TestCase {
	
	@Test
	public void testBPROP() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		FreeformNetwork network = NetworkUtil.createXORFreeformNetworkUntrained();

		MLTrain bprop = new FreeformBackPropagation(network, trainingData, 0.7, 0.9);
		NetworkUtil.testTraining(trainingData,bprop,0.01);
	}
	
	@Test
	public void testRPROP() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		FreeformNetwork network = NetworkUtil.createXORFreeformNetworkUntrained();

		MLTrain bprop = new FreeformResilientPropagation(network, trainingData);
		NetworkUtil.testTraining(trainingData,bprop,0.01);
	}
	
	@Test
	public void testAnneal() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);		
		FreeformNetwork network = NetworkUtil.createXORFreeformNetworkUntrained();
		CalculateScore score = new TrainingSetScore(trainingData);
		NeuralSimulatedAnnealing anneal = new NeuralSimulatedAnnealing(network,score,10,2,100);
		NetworkUtil.testTraining(trainingData,anneal,0.01);
	}
	
	@Test
	public void testGenetic() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);		
		CalculateScore score = new TrainingSetScore(trainingData);
		MLMethodGeneticAlgorithm genetic = new MLMethodGeneticAlgorithm(new MethodFactory(){
			@Override
			public MLMethod factor() {
				FreeformNetwork network = NetworkUtil.createXORFreeformNetworkUntrained();
				network.reset();
				return network;
			}}, score, 500);
		NetworkUtil.testTraining(trainingData,genetic,0.00001);
	}
}
