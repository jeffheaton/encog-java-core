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
package org.encog.neural.networks.training;

import junit.framework.TestCase;

import org.encog.ml.train.strategy.Greedy;
import org.encog.ml.train.strategy.HybridStrategy;
import org.encog.ml.train.strategy.ResetStrategy;
import org.encog.ml.train.strategy.StopTrainingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.training.strategy.SmartLearningRate;
import org.encog.neural.networks.training.strategy.SmartMomentum;
import org.encog.neural.pattern.FeedForwardPattern;
import org.junit.Assert;

public class TestStrategy extends TestCase{
	public void testDone()
	{
		StopTrainingStrategy strategy = new StopTrainingStrategy(0.01,2);
		MockTrain mock = new MockTrain();
		mock.addStrategy(strategy);
		mock.setError(0.05);
		Assert.assertFalse(strategy.shouldStop());
		mock.iteration();
		mock.iteration();
		mock.iteration();
		mock.iteration();
		Assert.assertTrue(strategy.shouldStop());
	}
	
	public void testGreedy()
	{
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(1);
		pattern.setOutputNeurons(1);
		BasicNetwork network = (BasicNetwork)pattern.generate();
		MockTrain.setFirstElement(3.0,network);
		
		MockTrain mock = new MockTrain();
		mock.setNetwork(network);
		Greedy strategy = new Greedy();
		mock.addStrategy(strategy);
		
		// simulate an improvement
		mock.setError(0.01);
		mock.simulate(0.04, 5.0);
		Assert.assertEquals(5.0, MockTrain.getFirstElement(network),0.1);
		
		// simulate a reverse
		mock.simulate(0.07, 6.0);
		Assert.assertEquals(5.0, MockTrain.getFirstElement(network),0.1);	
	}
	
	public void testHybrid()
	{
		MockTrain alt = new MockTrain();
		
		HybridStrategy strategy = new HybridStrategy(alt,0.001,2,5 );
		
		MockTrain mock = new MockTrain();
		mock.addStrategy(strategy);
		mock.setError(0.05);
		mock.iteration();
		mock.iteration();
		mock.iteration();
		mock.iteration();
		Assert.assertTrue(alt.wasUsed());
	}
	
	public void testReset()
	{
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(1);
		pattern.setOutputNeurons(1);
		BasicNetwork network = (BasicNetwork)pattern.generate();
		
		ResetStrategy strategy = new ResetStrategy(0.95,2);
		MockTrain mock = new MockTrain();
		mock.setNetwork(network);
		mock.addStrategy(strategy);
		mock.setError(0.07);
		MockTrain.setFirstElement(30.0,network);
		mock.iteration();
		Assert.assertTrue(MockTrain.getFirstElement(network)>20);
		mock.setError(0.99);
		mock.iteration();
		mock.iteration();
		mock.iteration();
		Assert.assertTrue(MockTrain.getFirstElement(network)<20);
	}
	
	public void testSmart()
	{
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(1);
		pattern.setOutputNeurons(1);
		BasicNetwork network = (BasicNetwork)pattern.generate();
		
		SmartLearningRate strategy1 = new SmartLearningRate();
		SmartMomentum strategy2 = new SmartMomentum();
		MockTrain mock = new MockTrain();
		mock.setNetwork(network);
		mock.setTraining(XOR.createXORDataSet());
		mock.addStrategy(strategy1);
		mock.addStrategy(strategy2);
		mock.setError(0.05);
		
		
		mock.simulate(0.04, 1);
		Assert.assertEquals(0.25, mock.getLearningRate(),0.1);
		mock.simulate(0.03, 1);
		mock.simulate(0.05, 1);
		Assert.assertEquals(0.2475, mock.getLearningRate(),0.1);
		
	}
}
