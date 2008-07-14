package org.encog.neural;

import org.encog.neural.feedforward.FeedforwardLayer;
import org.encog.neural.feedforward.FeedforwardNetwork;


public class XOR {

		public static double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 1.0 },
				{ 1.0, 1.0 } };

		public static double XOR_IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };
		
		public static boolean verifyXOR(FeedforwardNetwork network,double tolerance) 
		{
			for(int trainingSet=0;trainingSet<XOR.XOR_IDEAL.length;trainingSet++)
			{
				double actual[] = network.computeOutputs(XOR.XOR_INPUT[trainingSet]);
				
				for(int i=0;i<XOR.XOR_IDEAL[0].length;i++)
				{
					double diff = Math.abs(actual[i]-XOR.XOR_IDEAL[trainingSet][i]);
					if( diff>tolerance )
						return false;
				}
				
			}
			
			return true;
		}
		
		public static FeedforwardNetwork createThreeLayerNet()
		{
			FeedforwardNetwork network = new FeedforwardNetwork();
			network.addLayer(new FeedforwardLayer(2));
			network.addLayer(new FeedforwardLayer(3));
			network.addLayer(new FeedforwardLayer(1));
			network.reset();
			return network;
		}
}
