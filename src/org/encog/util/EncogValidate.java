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
