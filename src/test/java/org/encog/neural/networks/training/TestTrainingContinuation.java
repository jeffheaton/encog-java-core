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

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.junit.Assert;

public class TestTrainingContinuation extends TestCase {
	public void testContRPROP()
	{
		BasicNetwork network1 = NetworkUtil.createXORNetworkUntrained();
		BasicNetwork network2 = NetworkUtil.createXORNetworkUntrained();
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		// train network 1, no continue
		ResilientPropagation rprop1 = new ResilientPropagation(network1,trainingData);
		rprop1.iteration();
		rprop1.iteration();
		rprop1.iteration();
		rprop1.iteration();
		
		// train network 2, continue
		ResilientPropagation rprop2 = new ResilientPropagation(network2,trainingData);
		rprop2.iteration();
		rprop2.iteration();
		TrainingContinuation state = rprop2.pause();
		rprop2 = new ResilientPropagation(network2,trainingData);
		rprop2.resume(state);
		rprop2.iteration();
		rprop2.iteration();
		
		// verify weights are the same
		double[] weights1 = NetworkCODEC.networkToArray(network1);
		double[] weights2 = NetworkCODEC.networkToArray(network2);
		
		Assert.assertEquals(rprop1.getError(), rprop2.getError(), 0.01);
		Assert.assertEquals(weights1.length, weights2.length);
		Assert.assertArrayEquals(weights1, weights2, 0.01);
		
	}
	
	public void testContBackprop()
	{
		BasicNetwork network1 = NetworkUtil.createXORNetworkUntrained();
		BasicNetwork network2 = NetworkUtil.createXORNetworkUntrained();
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		// train network 1, no continue
		Backpropagation rprop1 = new Backpropagation(network1,trainingData,0.4,0.4);
		rprop1.iteration();
		rprop1.iteration();
		rprop1.iteration();
		rprop1.iteration();
		
		// train network 2, continue
		Backpropagation rprop2 = new Backpropagation(network2,trainingData,0.4,0.4);
		rprop2.iteration();
		rprop2.iteration();
		TrainingContinuation state = rprop2.pause();
		rprop2 = new Backpropagation(network2,trainingData,0.4,0.4);
		rprop2.resume(state);
		rprop2.iteration();
		rprop2.iteration();
		
		// verify weights are the same
		double[] weights1 = NetworkCODEC.networkToArray(network1);
		double[] weights2 = NetworkCODEC.networkToArray(network2);
		
		Assert.assertEquals(rprop1.getError(), rprop2.getError(), 0.01);
		Assert.assertEquals(weights1.length, weights2.length);
		Assert.assertArrayEquals(weights1, weights2, 0.01);
		
	}
}
