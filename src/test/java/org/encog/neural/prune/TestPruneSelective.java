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
package org.encog.neural.prune;

import junit.framework.TestCase;

import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.util.simple.EncogUtility;
import org.junit.Assert;

public class TestPruneSelective extends TestCase {
	
	private BasicNetwork obtainNetwork()
	{
		BasicNetwork network = EncogUtility.simpleFeedForward(2,3,0,4,false);
		double[] weights = { 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25 };
		NetworkCODEC.arrayToNetwork(weights, network);
		
		Assert.assertEquals(1.0, network.getWeight(1, 0, 0),0.01);		
		Assert.assertEquals(2.0, network.getWeight(1, 1, 0),0.01);
		Assert.assertEquals(3.0, network.getWeight(1, 2, 0),0.01);
		Assert.assertEquals(4.0, network.getWeight(1, 3, 0),0.01);
		
		Assert.assertEquals(5.0, network.getWeight(1, 0, 1),0.01);
		Assert.assertEquals(6.0, network.getWeight(1, 1, 1),0.01);
		Assert.assertEquals(7.0, network.getWeight(1, 2, 1),0.01);
		Assert.assertEquals(8.0, network.getWeight(1, 3, 1),0.01);
		
		Assert.assertEquals(9.0, network.getWeight(1, 0, 2),0.01);
		Assert.assertEquals(10.0, network.getWeight(1, 1, 2),0.01);
		Assert.assertEquals(11.0, network.getWeight(1, 2, 2),0.01);
		Assert.assertEquals(12.0, network.getWeight(1, 3, 2),0.01);
		
		Assert.assertEquals(13.0, network.getWeight(1, 0, 3),0.01);
		Assert.assertEquals(14.0, network.getWeight(1, 1, 3),0.01);
		Assert.assertEquals(15.0, network.getWeight(1, 2, 3),0.01);
		Assert.assertEquals(16.0, network.getWeight(1, 3, 3),0.01);
		
		Assert.assertEquals(17.0, network.getWeight(0, 0, 0),0.01);
		Assert.assertEquals(18.0, network.getWeight(0, 1, 0),0.01);
		Assert.assertEquals(19.0, network.getWeight(0, 2, 0),0.01);
		Assert.assertEquals(20.0, network.getWeight(0, 0, 1),0.01);
		Assert.assertEquals(21.0, network.getWeight(0, 1, 1),0.01);
		Assert.assertEquals(22.0, network.getWeight(0, 2, 1),0.01);
		
		Assert.assertEquals(20.0, network.getWeight(0, 0, 1),0.01);
		Assert.assertEquals(21.0, network.getWeight(0, 1, 1),0.01);
		Assert.assertEquals(22.0, network.getWeight(0, 2, 1),0.01);
		
		Assert.assertEquals(23.0, network.getWeight(0, 0, 2),0.01);
		Assert.assertEquals(24.0, network.getWeight(0, 1, 2),0.01);
		Assert.assertEquals(25.0, network.getWeight(0, 2, 2),0.01);

		
		return network;
	}
	
	private void checkWithModel(FlatNetwork model, FlatNetwork pruned)
	{
		Assert.assertEquals(model.getWeights().length, pruned.getWeights().length);
		Assert.assertArrayEquals(model.getContextTargetOffset(),pruned.getContextTargetOffset());
		Assert.assertArrayEquals(model.getContextTargetSize(),pruned.getContextTargetSize());
		Assert.assertArrayEquals(model.getLayerCounts(),pruned.getLayerCounts());
		Assert.assertArrayEquals(model.getLayerFeedCounts(),pruned.getLayerFeedCounts());
		Assert.assertArrayEquals(model.getLayerIndex(),pruned.getLayerIndex());
		Assert.assertEquals(model.getLayerOutput().length,pruned.getLayerOutput().length);
		Assert.assertArrayEquals(model.getWeightIndex(),pruned.getWeightIndex());
	}
	
	public void testPruneNeuronInput()
	{
		BasicNetwork network = obtainNetwork();
		Assert.assertEquals(2, network.getInputCount());
		PruneSelective prune = new PruneSelective(network);
		prune.prune(0, 1);
		Assert.assertEquals(22, network.encodedArrayLength());
		Assert.assertEquals(1,network.getLayerNeuronCount(0));
		Assert.assertEquals("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,19,20,22,23,25", network.dumpWeights());
		
		BasicNetwork model = EncogUtility.simpleFeedForward(1,3,0,4,false);
		checkWithModel(model.getStructure().getFlat(),network.getStructure().getFlat());
		Assert.assertEquals(1, network.getInputCount());
	}
	
	public void testPruneNeuronHidden()
	{
		BasicNetwork network = obtainNetwork();
		PruneSelective prune = new PruneSelective(network);
		prune.prune(1, 1);
		Assert.assertEquals(18, network.encodedArrayLength());
		Assert.assertEquals(2,network.getLayerNeuronCount(1));
		Assert.assertEquals("1,3,4,5,7,8,9,11,12,13,15,16,17,18,19,23,24,25", network.dumpWeights());
		
		BasicNetwork model = EncogUtility.simpleFeedForward(2,2,0,4,false);
		checkWithModel(model.getStructure().getFlat(),network.getStructure().getFlat());		
	}
	
	public void testPruneNeuronOutput()
	{
		BasicNetwork network = obtainNetwork();
		Assert.assertEquals(4, network.getOutputCount());
		PruneSelective prune = new PruneSelective(network);
		prune.prune(2, 1);
		Assert.assertEquals(21, network.encodedArrayLength());
		Assert.assertEquals(3,network.getLayerNeuronCount(2));
		Assert.assertEquals("1,2,3,4,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25", network.dumpWeights());
		
		BasicNetwork model = EncogUtility.simpleFeedForward(2,3,0,3,false);
		checkWithModel(model.getStructure().getFlat(),network.getStructure().getFlat());
		Assert.assertEquals(3, network.getOutputCount());
	}
	
	public void testNeuronSignificance()
	{
		BasicNetwork network = obtainNetwork();		
		PruneSelective prune = new PruneSelective(network);
		double inputSig = prune.determineNeuronSignificance(0, 1);
		double hiddenSig = prune.determineNeuronSignificance(1, 1);
		double outputSig = prune.determineNeuronSignificance(2, 1);
		Assert.assertEquals(63.0, inputSig,0.01);
		Assert.assertEquals(95.0, hiddenSig,0.01);
		Assert.assertEquals(26.0, outputSig,0.01);
	}
	
	public void testIncreaseNeuronCountHidden()
	{
		BasicNetwork network = XOR.createTrainedXOR();
		Assert.assertTrue( XOR.verifyXOR(network, 0.10) );
		PruneSelective prune = new PruneSelective(network);
		prune.changeNeuronCount(1, 5);
		
		BasicNetwork model = EncogUtility.simpleFeedForward(2,5,0,1,false);
		checkWithModel(model.getStructure().getFlat(),network.getStructure().getFlat());
		
		Assert.assertTrue( XOR.verifyXOR(network, 0.10) );
	}
	
	public void testIncreaseNeuronCountHidden2()
	{
		BasicNetwork network = EncogUtility.simpleFeedForward(5,6,0,2,true);
		PruneSelective prune = new PruneSelective(network);
		prune.changeNeuronCount(1, 60);
		
		BasicMLData input = new BasicMLData(5);
		BasicNetwork model = EncogUtility.simpleFeedForward(5,60,0,2,true);
		checkWithModel(model.getStructure().getFlat(),network.getStructure().getFlat());
		model.compute(input);
		network.compute(input);
	}
	
	public void testRandomizeNeuronInput()
	{
		double[] d = { 0,0,0,0,0,0,0,0,0,0,0,0,0 };
		BasicNetwork network = EncogUtility.simpleFeedForward(2,3,0,1,false);
		NetworkCODEC.arrayToNetwork(d, network);
		PruneSelective prune = new PruneSelective(network);
		prune.randomizeNeuron(100, 100, 0,1);
		Assert.assertEquals("0,0,0,0,0,100,0,0,100,0,0,100,0", network.dumpWeights());
	}
	
	public void testRandomizeNeuronHidden()
	{
		double[] d = { 0,0,0,0,0,0,0,0,0,0,0,0,0 };
		BasicNetwork network = EncogUtility.simpleFeedForward(2,3,0,1,false);
		NetworkCODEC.arrayToNetwork(d, network);
		PruneSelective prune = new PruneSelective(network);
		prune.randomizeNeuron(100, 100, 1,1);
		Assert.assertEquals("0,100,0,0,0,0,0,100,100,100,0,0,0", network.dumpWeights());
	}
	
	public void testRandomizeNeuronOutput()
	{
		double[] d = { 0,0,0,0,0,0,0,0,0,0,0,0,0 };
		BasicNetwork network = EncogUtility.simpleFeedForward(2,3,0,1,false);
		NetworkCODEC.arrayToNetwork(d, network);
		PruneSelective prune = new PruneSelective(network);
		prune.randomizeNeuron(100, 100, 2,0);
		Assert.assertEquals("100,100,100,100,0,0,0,0,0,0,0,0,0", network.dumpWeights());
	}
}
