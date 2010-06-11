/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
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
