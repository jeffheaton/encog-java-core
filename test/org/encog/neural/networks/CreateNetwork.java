package org.encog.neural.networks;

import org.encog.matrix.Matrix;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

public class CreateNetwork {
	
	public static final Double[] RANDOM_NET = {-0.8289675647834567, 0.41428419615431555, -0.6631344291596013, -0.6347844053306126, 0.8725933251770621, 0.20730871363234438, 0.0693984428627592, 0.39495816342847045, 0.2876293823661842, -0.8091007635627903, 0.5170049536924719, -0.8775363794949156, 0.02786434379814584, -0.7373784461103059, 0.7670893161435932};
	
	public static BasicNetwork createXORNetworkUntrained()
	{
		// random matrix data.  However, it provides a constant starting point 
		// for the unit tests.
		
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(2));
		network.addLayer(new BasicLayer(3));
		network.addLayer(new BasicLayer(1));
		network.getStructure().finalizeStructure();
		NetworkCODEC.arrayToNetwork(RANDOM_NET, network);
		
		return network;
	}
}
