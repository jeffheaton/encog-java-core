package org.encog.neural.persist;

import java.util.Iterator;

import org.encog.neural.XOR;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.Train;
import org.encog.neural.networks.feedforward.FeedforwardLayer;
import org.encog.neural.networks.feedforward.FeedforwardNetwork;
import org.encog.neural.networks.feedforward.train.backpropagation.Backpropagation;

import junit.framework.TestCase;

public class TestPersist extends TestCase {
	private FeedforwardNetwork createNetwork() 
	{
		FeedforwardNetwork network = new FeedforwardNetwork();
		network.addLayer(new FeedforwardLayer(2));
		network.addLayer(new FeedforwardLayer(3));
		network.addLayer(new FeedforwardLayer(1));
		network.reset();
		return network;
	}
	
	public void testBackpropagation() throws Throwable
	{
		NeuralDataSet trainingData = new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
		
		FeedforwardNetwork network = createNetwork();
		Train train = new Backpropagation(network, trainingData, 0.7, 0.9); 	

		for (int i = 0; i < 5000; i++) 
		{
			train.iteration();
			network = train.getNetwork();
		}
		
		TestCase.assertTrue("Error too high for backpropagation",train.getError()<0.1);
		TestCase.assertTrue("XOR outputs not correct",XOR.verifyXOR(network, 0.1));
		
		EncogPersistedCollection encog = new EncogPersistedCollection();
		encog.add(network);
		encog.save("c:\\encog.xml");
		
		EncogPersistedCollection encog2 =  new EncogPersistedCollection();
		encog2.load("c:\\encog.xml");
		System.out.println(encog2.getEncogVersion());
		FeedforwardNetwork n = (FeedforwardNetwork) encog2.getList().get(0);
		TestCase.assertTrue("Error too high for load",n.calculateError(trainingData)<0.1);

	}
		
}
