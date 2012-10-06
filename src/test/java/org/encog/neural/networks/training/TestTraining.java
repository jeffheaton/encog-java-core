/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.neural.networks.training;

import junit.framework.TestCase;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.MLMethod;
import org.encog.ml.MethodFactory;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.genetic.MLMethodGeneticAlgorithm;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.networks.training.genetic.NeuralGeneticAlgorithm;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.neural.networks.training.pnn.TrainBasicPNN;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.manhattan.ManhattanPropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.propagation.scg.ScaledConjugateGradient;
import org.encog.neural.pnn.BasicPNN;
import org.encog.neural.pnn.PNNKernelType;
import org.encog.neural.pnn.PNNOutputMode;
import org.junit.Test;

public class TestTraining extends TestCase   {

	
	@Test
	public void testRPROP() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
		MLTrain rprop = new ResilientPropagation(network, trainingData);
		NetworkUtil.testTraining(rprop,0.03);
	}
	
	@Test
	public void testLMA() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
		MLTrain rprop = new LevenbergMarquardtTraining(network, trainingData);
		NetworkUtil.testTraining(rprop,0.03);
	}
	
	@Test
	public void testBPROP() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();

		MLTrain bprop = new Backpropagation(network, trainingData, 0.7, 0.9);
		NetworkUtil.testTraining(bprop,0.01);
	}
	
	@Test
	public void testManhattan() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
		MLTrain bprop = new ManhattanPropagation(network, trainingData, 0.01);
		NetworkUtil.testTraining(bprop,0.01);
	}
	
	@Test
	public void testSCG() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
		MLTrain bprop = new ScaledConjugateGradient(network, trainingData);
		NetworkUtil.testTraining(bprop,0.04);
	}
	
	@Test
	public void testAnneal() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);		
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
		CalculateScore score = new TrainingSetScore(trainingData);
		NeuralSimulatedAnnealing anneal = new NeuralSimulatedAnnealing(network,score,10,2,100);
		NetworkUtil.testTraining(anneal,0.01);
	}
	
	@Test
	public void testGenetic() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);		
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
		CalculateScore score = new TrainingSetScore(trainingData);
		NeuralGeneticAlgorithm genetic = new NeuralGeneticAlgorithm(network, new RangeRandomizer(-1,1), score, 500,0.1,0.25);
		NetworkUtil.testTraining(genetic,0.00001);
	}
	
	@Test
	public void testMLMethodGenetic() throws Throwable
	{
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);		
		CalculateScore score = new TrainingSetScore(trainingData);
		MLMethodGeneticAlgorithm genetic = new MLMethodGeneticAlgorithm(new MethodFactory(){
			@Override
			public MLMethod factor() {
				BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
				network.reset();
				return network;
			}}, score, 500,0.1,0.25);
		NetworkUtil.testTraining(genetic,0.00001);
	}
	
	@Test
	public void testRegPNN() throws Throwable
	{
		
		PNNOutputMode mode = PNNOutputMode.Regression;
		BasicPNN network = new BasicPNN(PNNKernelType.Gaussian, mode, 2, 1);

		BasicMLDataSet trainingSet = new BasicMLDataSet(XOR.XOR_INPUT,
				XOR.XOR_IDEAL);

		TrainBasicPNN train = new TrainBasicPNN(network, trainingSet);
		train.iteration();
		
		XOR.verifyXOR(network, 0.01);
	}
	
	@Test
	public void testClassifyPNN() throws Throwable
	{
		
		PNNOutputMode mode = PNNOutputMode.Classification;
		BasicPNN network = new BasicPNN(PNNKernelType.Gaussian, mode, 2, 2);

		BasicMLDataSet trainingSet = new BasicMLDataSet(XOR.XOR_INPUT,
				XOR.XOR_IDEAL);

		TrainBasicPNN train = new TrainBasicPNN(network, trainingSet);
		train.iteration();
		
		XOR.verifyXOR(network, 0.01);
	}
}
