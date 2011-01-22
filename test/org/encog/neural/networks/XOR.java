/*
 * Encog(tm) Core v3.0 Unit Test - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.neural.networks;

import junit.framework.TestCase;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;



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
		
		public static NeuralDataSet createXORDataSet()
		{
			return new BasicNeuralDataSet(XOR.XOR_INPUT,XOR.XOR_IDEAL);
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
			network.addLayer(new BasicLayer(2));
			network.addLayer(new BasicLayer(3));
			network.addLayer(new BasicLayer(1));
			network.getStructure().finalizeStructure();
			network.reset();
			return network;
		}
}
