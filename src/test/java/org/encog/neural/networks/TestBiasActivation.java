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
package org.encog.neural.networks;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

public class TestBiasActivation extends TestCase {
	
	public void testLayerOutput()
	{
		Layer layer1, layer2;
		BasicNetwork network = new BasicNetwork();
		network.addLayer(layer1 = new BasicLayer(null, true,2));
		network.addLayer(layer2 = new BasicLayer(new ActivationSigmoid(), true,4));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false,1));
		int i = 0;
		i++;
		layer1.setBiasActivation(0.5);
		layer2.setBiasActivation(-1.0);
		network.getStructure().finalizeStructure();
		network.reset();
		
		FlatNetwork flat = network.getStructure().getFlat();
		
		Assert.assertNotNull(flat);
		double[] layerOutput = flat.getLayerOutput();
		Assert.assertEquals(-1, layerOutput[5], 2 );
		Assert.assertEquals(0.5, layerOutput[8], 2 );	
	}
	
	public void testLayerOutputPostFinalize()
	{
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true,2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true,4));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false,1));

		network.getStructure().finalizeStructure();
		network.reset();
		
		network.setLayerBiasActivation(0,0.5);
		network.setLayerBiasActivation(1,-1.0);
		
		FlatNetwork flat = network.getStructure().getFlat();
		
		Assert.assertNotNull(flat);
		double[] layerOutput = flat.getLayerOutput();
		Assert.assertEquals(layerOutput[5], -1.0);
		Assert.assertEquals(layerOutput[8], 0.5);	
	}
	
	public void testTrain()
	{
		BasicNetwork network1 = NetworkUtil.createXORNetworkUntrained();
		BasicNetwork network2 = (BasicNetwork)network1.clone();
		BasicNetwork network3 = (BasicNetwork)network1.clone();
		network2.setBiasActivation(-1);
		network3.setBiasActivation(0.5);
		
		MLDataSet trainingData = new BasicMLDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		MLTrain rprop1 = new ResilientPropagation(network1, trainingData);
		MLTrain rprop2 = new ResilientPropagation(network2, trainingData);
		MLTrain rprop3 = new ResilientPropagation(network3, trainingData);

		NetworkUtil.testTraining(trainingData,rprop1,0.03);
		NetworkUtil.testTraining(trainingData,rprop2,0.01);
		NetworkUtil.testTraining(trainingData,rprop3,0.01);
		
		double[] w1 = NetworkCODEC.networkToArray(network1);
		double[] w2 = NetworkCODEC.networkToArray(network2);
		double[] w3 = NetworkCODEC.networkToArray(network3);
		
		Assert.assertTrue(Math.abs(w1[0]-w2[0])>Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertTrue(Math.abs(w2[0]-w3[0])>Encog.DEFAULT_DOUBLE_EQUAL);
		
	}
}
