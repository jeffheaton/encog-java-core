/*
 * Encog(tm) Unit Tests v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.logging.Logging;

public class TestBiasActivation extends TestCase {
	
	public void testLayerOutput()
	{
		Layer hidden, output;
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, false,2));
		network.addLayer(hidden = new BasicLayer(new ActivationSigmoid(), true,4));
		network.addLayer(output = new BasicLayer(new ActivationSigmoid(), true,1));
		network.reset();
		hidden.setBiasActivation(0.5);
		output.setBiasActivation(-1.0);
		network.getStructure().finalizeStructure();
		
		FlatNetwork flat = network.getStructure().getFlat();
		
		Assert.assertNotNull(flat);
		double[] layerOutput = flat.getLayerOutput();
		Assert.assertEquals(layerOutput[5], -1.0);
		Assert.assertEquals(layerOutput[8], 0.5);	
	}
	
	public void testTrain()
	{
		Logging.stopConsoleLogging();
		BasicNetwork network1 = NetworkUtil.createXORNetworkUntrained();
		BasicNetwork network2 = (BasicNetwork)network1.clone();
		BasicNetwork network3 = (BasicNetwork)network1.clone();
		network2.setBiasActivation(-1);
		network2.getStructure().finalizeStructure();
		network3.setBiasActivation(0.5);
		network3.getStructure().finalizeStructure();
		
		NeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		Train rprop1 = new ResilientPropagation(network1, trainingData);
		Train rprop2 = new ResilientPropagation(network2, trainingData);
		Train rprop3 = new ResilientPropagation(network3, trainingData);

		NetworkUtil.testTraining(rprop1,0.03);
		NetworkUtil.testTraining(rprop2,0.01);
		NetworkUtil.testTraining(rprop3,0.01);
		
		network1.getStructure().updateFlatNetwork();
		network2.getStructure().updateFlatNetwork();
		network3.getStructure().updateFlatNetwork();
		
		double[] w1 = NetworkCODEC.networkToArray(network1);
		double[] w2 = NetworkCODEC.networkToArray(network2);
		double[] w3 = NetworkCODEC.networkToArray(network3);
		
		Assert.assertTrue(Math.abs(w1[0]-w2[0])>Encog.DEFAULT_DOUBLE_EQUAL);
		Assert.assertTrue(Math.abs(w2[0]-w3[0])>Encog.DEFAULT_DOUBLE_EQUAL);
		
	}
}
