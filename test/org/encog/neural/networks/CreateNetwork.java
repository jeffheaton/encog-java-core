package org.encog.neural.networks;

import org.encog.matrix.Matrix;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.util.randomize.ConsistentRandomizer;

public class CreateNetwork {
	
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
}
