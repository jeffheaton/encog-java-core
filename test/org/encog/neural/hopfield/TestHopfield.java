package org.encog.neural.hopfield;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.feedforward.FeedforwardNetwork;
import org.encog.neural.networks.hopfield.HopfieldLayer;
import org.encog.neural.networks.hopfield.TrainHopfield;
import org.encog.neural.networks.Train;

import junit.framework.TestCase;

public class TestHopfield extends TestCase {
	public void testHopfield() throws Throwable
	{		
		boolean input[] = { true, false, true, false };
		
		FeedforwardNetwork network = new FeedforwardNetwork();
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
			FeedforwardNetwork network = new FeedforwardNetwork();
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
