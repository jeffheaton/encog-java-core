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

package org.encog.neural.networks.structure;

import java.util.List;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.neural.pattern.JordanPattern;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestNeuralStructure extends TestCase {
	public void testStructureFeedForward()
	{
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.addHiddenLayer(3);
		pattern.setOutputNeurons(4);
		BasicNetwork network = pattern.generate();
		network.getStructure().sort();
		List<Layer> layerList = network.getStructure().getLayers();
		Assert.assertEquals(4,layerList.get(0).getNeuronCount());
		Assert.assertEquals(3,layerList.get(1).getNeuronCount());
		Assert.assertEquals(2,layerList.get(2).getNeuronCount());
		Assert.assertEquals(1,layerList.get(3).getNeuronCount());
		
		List<Synapse> synapseList = network.getStructure().getSynapses();
		
		Assert.assertEquals(4,synapseList.get(0).getToNeuronCount());		
		Assert.assertEquals(3,synapseList.get(0).getFromNeuronCount());
		Assert.assertEquals(3,synapseList.get(1).getToNeuronCount());
		Assert.assertEquals(2,synapseList.get(1).getFromNeuronCount());
		Assert.assertEquals(2,synapseList.get(2).getToNeuronCount());
		Assert.assertEquals(1,synapseList.get(2).getFromNeuronCount());
		
	}
	
	public void testStructureElman()
	{
		ElmanPattern pattern = new ElmanPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.setOutputNeurons(3);
		BasicNetwork network = pattern.generate();
		List<Layer> list = network.getStructure().getLayers();
		Assert.assertEquals(3,list.get(0).getNeuronCount());
		Assert.assertTrue(list.get(1) instanceof BasicLayer );
		Assert.assertTrue(list.get(2) instanceof ContextLayer );
		Assert.assertEquals(2,list.get(1).getNeuronCount());
		Assert.assertEquals(2,list.get(2).getNeuronCount());
		Assert.assertEquals(1,list.get(3).getNeuronCount());
		
		List<Synapse> synapseList = network.getStructure().getSynapses();		
		Assert.assertEquals(3,synapseList.get(0).getToNeuronCount());		
		Assert.assertEquals(2,synapseList.get(0).getFromNeuronCount());
		Assert.assertEquals(2,synapseList.get(1).getToNeuronCount());		
		Assert.assertEquals(2,synapseList.get(1).getFromNeuronCount());
		Assert.assertEquals(2,synapseList.get(2).getToNeuronCount());		
		Assert.assertEquals(1,synapseList.get(2).getFromNeuronCount());

	}
	
	public void testStructureJordan()
	{
		JordanPattern pattern = new JordanPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.setOutputNeurons(3);
		BasicNetwork network = pattern.generate();
		List<Layer> list = network.getStructure().getLayers();
		Assert.assertEquals(3,list.get(0).getNeuronCount());
		Assert.assertTrue(list.get(1) instanceof BasicLayer );
		Assert.assertTrue(list.get(2) instanceof ContextLayer );
		Assert.assertEquals(2,list.get(1).getNeuronCount());
		Assert.assertEquals(3,list.get(2).getNeuronCount());
		Assert.assertEquals(1,list.get(3).getNeuronCount());
		
		List<Synapse> synapseList = network.getStructure().getSynapses();		
		Assert.assertEquals(3,synapseList.get(0).getToNeuronCount());		
		Assert.assertEquals(2,synapseList.get(0).getFromNeuronCount());
		Assert.assertEquals(2,synapseList.get(1).getToNeuronCount());		
		Assert.assertEquals(3,synapseList.get(1).getFromNeuronCount());
		Assert.assertEquals(2,synapseList.get(2).getToNeuronCount());		
		Assert.assertEquals(1,synapseList.get(2).getFromNeuronCount());

	}
}
