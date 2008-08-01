package org.encog.neural.feedforward;

import org.encog.neural.XOR;
import org.encog.neural.networks.BasicNetwork;

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
