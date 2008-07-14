package org.encog.neural.hopfield;

import org.encog.neural.NeuralNetworkError;

import junit.framework.TestCase;

public class TestHopfield extends TestCase {
	public void testHopfield() throws Throwable
	{
		boolean input[] = { true, false, true, false };
		HopfieldNetwork network = new HopfieldNetwork(4);
		network.train(input);
		boolean[] output = network.present(input);
		TestCase.assertTrue(output[0]);
		TestCase.assertFalse(output[1]);
		TestCase.assertTrue(output[2]);
		TestCase.assertFalse(output[3]);
	}
	
	public void testInvalidTrain() throws Throwable
	{

		try {
			boolean input[] = { true, false, true };
			HopfieldNetwork network = new HopfieldNetwork(4);
			network.train(input);
			TestCase.assertTrue(false);
		}
		catch(NeuralNetworkError e)
		{
			
		}
		
	}
}
