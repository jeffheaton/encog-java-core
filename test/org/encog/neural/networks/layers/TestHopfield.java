package org.encog.neural.networks.layers;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.layers.HopfieldLayer;
import org.encog.neural.networks.training.hopfield.TrainHopfield;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.Train;

import junit.framework.TestCase;

public class TestHopfield extends TestCase {
	public void testHopfield() throws Throwable
	{		
		boolean input[] = { true, false, true, false };
		
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new HopfieldLayer(4));
		
		NeuralData data = new BiPolarNeuralData(input);
		Train train = new TrainHopfield(data,network);
		train.iteration();
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
			NeuralData data = new BiPolarNeuralData(input);
			BasicNetwork network = new BasicNetwork();
			network.addLayer( new HopfieldLayer(4) );
			Train train = new TrainHopfield(data,network);
			train.iteration();
			TestCase.assertTrue(false);
		}
		catch(NeuralNetworkError e)
		{
			
		}
		
	}
}
