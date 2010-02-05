/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version, Unit Tests
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.neural.networks.training;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.strategy.Greedy;
import org.encog.neural.networks.training.strategy.HybridStrategy;
import org.encog.neural.networks.training.strategy.ResetStrategy;
import org.encog.neural.networks.training.strategy.SmartLearningRate;
import org.encog.neural.networks.training.strategy.SmartMomentum;
import org.encog.neural.networks.training.strategy.StopTrainingStrategy;
import org.encog.neural.pattern.FeedForwardPattern;
import org.junit.Assert;

import junit.framework.TestCase;

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
