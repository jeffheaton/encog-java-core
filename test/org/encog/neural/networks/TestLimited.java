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

import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.logging.Logging;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestLimited extends TestCase {
	
	public void testLimited()
	{
		Logging.stopConsoleLogging();
		NeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();		
		
		Synapse synapse = network.getStructure().getSynapses().get(0);
		
		ResilientPropagation rprop = new ResilientPropagation(network,trainingData);
		rprop.iteration();
		rprop.iteration();
		network.enableConnection(synapse, 0, 0, false);
		network.enableConnection(synapse, 1, 0, false);
		Assert.assertEquals(synapse.getMatrix().get(0,0),0.0,0.1);
		Assert.assertTrue(network.getStructure().isConnectionLimited());
		network.getStructure().updateFlatNetwork();
		Assert.assertEquals(0.0, network.getStructure().getFlat().getWeights()[0], 0.01);
		Assert.assertEquals(0.0, network.getStructure().getFlat().getWeights()[1], 0.01);
		rprop.iteration();
		rprop.iteration();
		rprop.iteration();
		rprop.iteration();
		// these connections were removed, and should not have been "trained"
		Assert.assertEquals(0.0, network.getStructure().getFlat().getWeights()[0], 0.01);
		Assert.assertEquals(0.0, network.getStructure().getFlat().getWeights()[1], 0.01);		
		rprop.finishTraining();
	}
}
