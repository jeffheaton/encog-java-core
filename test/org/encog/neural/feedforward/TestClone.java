package org.encog.neural.feedforward;

import org.encog.neural.XOR;

import junit.framework.TestCase;

public class TestClone extends TestCase {

	public void testClone() throws Throwable
	{
		FeedforwardNetwork source = XOR.createThreeLayerNet();
		source.reset();
		
		FeedforwardNetwork target = (FeedforwardNetwork)source.clone();
		
		TestCase.assertTrue(target.equals(source));
	}
	
}
