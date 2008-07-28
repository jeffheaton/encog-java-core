package org.encog.neural.hopfield;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.hopfield.HopfieldNetwork;

import junit.framework.TestCase;

public class TestHopfield extends TestCase {
	public void testHopfield() throws Throwable
	{
		boolean input[] = { true, false, true, false };
		HopfieldNetwork network = new HopfieldNetwork(4);
		network.train(new BiPolarNeuralData(input));
		BiPolarNeuralData output = (BiPolarNeuralData) network.compute(new BiPolarNeuralData(input));
		TestCase.assertTrue(output.getBoolean(0));
		TestCase.assertFalse(output.getBoolean(1));
		TestCase.assertTrue(output.getBoolean(2));
		TestCase.assertFalse(output.getBoolean(3));
	}
	
	public void testInvalidTrain() throws Throwable
	{

		try {
			boolean input[] = { true, false, true };
			HopfieldNetwork network = new HopfieldNetwork(4);
			network.train(new BiPolarNeuralData(input));
			TestCase.assertTrue(false);
		}
		catch(NeuralNetworkError e)
		{
			
		}
		
	}
}
