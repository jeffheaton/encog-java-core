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

package org.encog.neural.prune;

import junit.framework.Assert;

import org.encog.NullStatusReportable;
import org.encog.engine.StatusReportable;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.NetworkUtil;
import org.encog.neural.networks.XOR;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.util.StatusCounter;
import org.junit.Test;

public class TestPrune {
	@Test
	public void testToString() throws Throwable
	{
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
		Layer layer = network.getLayer(BasicNetwork.TAG_INPUT);
		layer.toString();
	}
	
	@Test
	public void testCounts() throws Throwable
	{
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
		Assert.assertEquals(6, network.calculateNeuronCount());		
	}

	@Test
	public void testPrune() throws Throwable
	{
		BasicNetwork network = NetworkUtil.createXORNetworkUntrained();
		Layer inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		Layer hiddenLayer = inputLayer.getNext().get(0).getToLayer();
		
		Assert.assertEquals(3,hiddenLayer.getNeuronCount());

		PruneSelective prune = new PruneSelective(network);
		prune.prune(hiddenLayer, 1);
		
		Assert.assertEquals(2,hiddenLayer.getNeuronCount());
	}
	
	@Test
	public void testIncPrune()
	{
		StatusCounter counter = new StatusCounter();
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(2);
		pattern.setOutputNeurons(1);
		pattern.setActivationFunction(new ActivationSigmoid());
		NeuralDataSet training = XOR.createXORDataSet();
		PruneIncremental inc = new PruneIncremental(training,pattern,10,1,5,counter);
		inc.addHiddenLayer(1, 4);
		inc.addHiddenLayer(0, 4);
		inc.process();
		Assert.assertEquals(20, counter.getCount());
	}
}
