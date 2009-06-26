package org.encog.persist;

import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.RadialBasisPattern;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestPersist extends TestCase {
	
	private BasicNetwork getRBF()
	{
		RadialBasisPattern pattern = new RadialBasisPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.setOutputNeurons(3);
		BasicNetwork net = pattern.generate();
		return net;
	}
	
	private BasicNetwork getElman()
	{
		ElmanPattern pattern = new ElmanPattern();
		pattern.setInputNeurons(1);
		pattern.addHiddenLayer(2);
		pattern.setOutputNeurons(3);
		pattern.setActivationFunction(new ActivationSigmoid());
		BasicNetwork net = pattern.generate();
		return net;
	}
	
	public void testPersist()
	{
		EncogPersistedCollection encog = 
			new EncogPersistedCollection("encogtest.eg");
		encog.create();
		BasicNetwork net1 = getRBF();
		BasicNetwork net2 = getElman();
		encog.add("rbf", net1);
		encog.add("elman", net2);
		
		net1 = (BasicNetwork)encog.find("rbf");
		net2 = (BasicNetwork)encog.find("elman");
		
		Assert.assertNotNull(net1);
		Assert.assertNotNull(net2);
		
	}
}
