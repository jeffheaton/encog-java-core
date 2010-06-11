/*
 * Encog(tm) Core v2.5 
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

package org.encog.neural.prune;

import junit.framework.Assert;

import org.encog.NullStatusReportable;
import org.encog.StatusReportable;
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
