package org.encog.neural.networks;

import junit.framework.TestCase;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.FeedforwardLayer;


public class XOR {

		public static double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 1.0 },
				{ 1.0, 1.0 } };

		public static double XOR_IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };
		
		public static boolean verifyXOR(BasicNetwork network,double tolerance) 
		{
			for(int trainingSet=0;trainingSet<XOR.XOR_IDEAL.length;trainingSet++)
			{
				NeuralData actual = network.compute(new BasicNeuralData(XOR.XOR_INPUT[trainingSet]));
				
				for(int i=0;i<XOR.XOR_IDEAL[0].length;i++)
				{
					double diff = Math.abs(actual.getData(i)-XOR.XOR_IDEAL[trainingSet][i]);
					if( diff>tolerance )
						return false;
				}
				
			}
			
			return true;
		}
		
		public static void testXORDataSet(NeuralDataSet set)
		{
			int row = 0;
			for(NeuralDataPair item: set)
			{
				for(int i=0;i<XOR.XOR_INPUT[0].length;i++)
				{
					TestCase.assertEquals(item.getInput().getData(i), 
							XOR.XOR_INPUT[row][i]);
				}
				
				for(int i=0;i<XOR.XOR_IDEAL[0].length;i++)
				{
					TestCase.assertEquals(item.getIdeal().getData(i), 
							XOR.XOR_IDEAL[row][i]);
				}
				
				row++;
			}
		}
		
		public static BasicNetwork createThreeLayerNet()
		{
			BasicNetwork network = new BasicNetwork();
			network.addLayer(new FeedforwardLayer(2));
			network.addLayer(new FeedforwardLayer(3));
			network.addLayer(new FeedforwardLayer(1));
			network.reset();
			return network;
		}
}
