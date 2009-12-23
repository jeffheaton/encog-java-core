package org.encog.neural.networks;

import junit.framework.Assert;

import org.encog.matrix.Matrix;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.Train;
import org.encog.util.randomize.ConsistentRandomizer;

public class NetworkUtil {
	
	public static BasicNetwork createXORNetworkUntrained()
	{
		// random matrix data.  However, it provides a constant starting point 
		// for the unit tests.
		
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,2));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
		network.addLayer(new BasicLayer(new ActivationSigmoid(),true,1));
		network.getStructure().finalizeStructure();
		(new ConsistentRandomizer(-1,1)).randomize(network);
		
		return network;
	}
	
	public static void testTraining(Train train, double requiredImprove)
	{
		train.iteration();
		double error1 = train.getError();
		
		for(int i=0;i<10;i++)
			train.iteration();
		
		double error2 = train.getError();
		
		double improve = (error1-error2)/error1;
		Assert.assertTrue("Improve rate too low for " + train.getClass().getSimpleName() + 
				",Improve="+improve+",Needed="+requiredImprove, improve>=requiredImprove);
	}
}


