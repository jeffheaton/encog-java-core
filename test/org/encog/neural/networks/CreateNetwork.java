package org.encog.neural.networks;

import org.encog.matrix.Matrix;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.FeedforwardLayer;

public class CreateNetwork {
	
	public static BasicNetwork createXORNetworkUntrained()
	{
		// random matrix data.  However, it provides a constant starting point 
		// for the unit tests.
		double matrixData1[][] = 
		{
			{-0.8026145065833352, 0.48730020258365925, -0.29670931365567577 },
			{0.07689650585681851, -0.513969748944711, 0.11858304184009771},
			{-0.4485719795825909, 0.15435275595196507, 0.17655902338449336} };

		double matrixData2[][] = 
		{
			{0.024694322443027827},
			{-0.0447166248226063},
			{0.9000418882323729},
			{0.38999333206070275} };
		
		Matrix matrix1 = new Matrix(matrixData1);
		Matrix matrix2 = new Matrix(matrixData2);
		
		FeedforwardLayer layer1,layer2;
		BasicNetwork network = new BasicNetwork();
		network.addLayer(layer1 = new FeedforwardLayer(2));
		network.addLayer(layer2 = new FeedforwardLayer(3));
		network.addLayer(new FeedforwardLayer(1));
		
		//layer1.setMatrix(matrix1);
		//layer2.setMatrix(matrix2);
		
		return network;
	}
}
