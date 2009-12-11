package org.encog.neural.networks.structure;

import java.util.List;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.FeedForwardPattern;

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
}
