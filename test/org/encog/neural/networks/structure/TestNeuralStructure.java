/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
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
package org.encog.neural.networks.structure;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.neural.pattern.JordanPattern;

public class TestNeuralStructure extends TestCase {
	public void testStructureFeedForward()
	{
		FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.addHiddenLayer(3);
		pattern.setOutputNeurons(4);
		BasicNetwork network = (BasicNetwork)pattern.generate();
		network.getStructure().finalizeStructure();
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
	
	public void testOrder()
	{
		Layer l1,l2,l3,l4,l5;
		final BasicNetwork net = new BasicNetwork();
	    net.addLayer(l1 = new BasicLayer(new ActivationTANH(), false, 1)); // L1
	    net.addLayer(l2 = new BasicLayer(new ActivationTANH(), false, 2)); // L2
	    net.addLayer(l3 = new BasicLayer(new ActivationTANH(), false, 3)); // L3
	    net.addLayer(l4 = new BasicLayer(new ActivationTANH(), false, 4)); // L4
	    net.addLayer(l5 = new BasicLayer(new ActivationLinear(), false, 5)); // L5
	    net.getStructure().finalizeStructure();
	    net.reset();
	    
	    List<Layer> layers = net.getStructure().getLayers();
	    System.out.println(layers.toString());
	    Assert.assertEquals(l5, layers.get(0));
	    Assert.assertEquals(l4, layers.get(1));
	    Assert.assertEquals(l3, layers.get(2));
	    Assert.assertEquals(l2, layers.get(3));
	    Assert.assertEquals(l1, layers.get(4));
	}
	
	public void testStructureElman()
	{
		ElmanPattern pattern = new ElmanPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.setOutputNeurons(3);
		BasicNetwork network = (BasicNetwork)pattern.generate();
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
		BasicNetwork network = (BasicNetwork)pattern.generate();
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
