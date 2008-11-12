package org.encog.neural.networks.layers;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.XOR;

import junit.framework.TestCase;

public class TestClone extends TestCase {

	public void testClone() throws Throwable
	{
		BasicNetwork source = XOR.createThreeLayerNet();
		source.reset();
		
		BasicNetwork target = (BasicNetwork)source.clone();
		
		TestCase.assertTrue(target.equals(source));
	}
	
}
