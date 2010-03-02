/*
 * Encog(tm) Core v2.4
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

package org.encog.util;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;

public class EncogValidate {
	
	public static void validateNetworkForTraining(BasicNetwork network, NeuralDataSet training)
	{
		Layer inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
		Layer outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);
		
		if( inputLayer==null )
		{
			throw new NeuralNetworkError("This operation requires that the neural network have an input layer."); 
		}
		
		if( outputLayer==null )
		{
			throw new NeuralNetworkError("This operation requires that the neural network have an output layer."); 
		}
		
		if( inputLayer.getNeuronCount()!=training.getInputSize())
		{
			throw new NeuralNetworkError("The input layer size of " 
					+ inputLayer.getNeuronCount() 
					+ " must match the training input size of " 
					+ training.getInputSize() + ".");
		}
		
		if( training.getIdealSize()>0 &&
			outputLayer.getNeuronCount()!=training.getIdealSize())
		{
			throw new NeuralNetworkError("The output layer size of " 
					+ inputLayer.getNeuronCount() 
					+ " must match the training input size of " 
					+ training.getIdealSize() + ".");
		}
	}
}
