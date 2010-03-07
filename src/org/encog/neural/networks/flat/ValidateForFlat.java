package org.encog.neural.networks.flat;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;

public class ValidateForFlat {
	public static void validateNetwork(BasicNetwork network)
	{
		ActivationFunction lastActivation = null;
		
		for( Layer layer: network.getStructure().getLayers() )
		{
			// only feedforward
			if( layer.getNext().size()>1 )
			{
				throw new NeuralNetworkError("To convert to flat a network must be feedforward only.");
			}
			
			if( !(layer.getActivationFunction() instanceof ActivationSigmoid) &&
				!(layer.getActivationFunction() instanceof ActivationTANH) )
			{
				throw new NeuralNetworkError("To convert to flat a network must only use sigmoid and tanh activation.");
			}
			
			if( lastActivation!=null )
			{
				if( layer.getActivationFunction().getClass()!=lastActivation.getClass())
				{
					throw new NeuralNetworkError("To convert to flat, a network must use the same activation function on each layer.");
				}
			}
			
			if( !layer.hasThreshold() && lastActivation!=null )
			{
				throw new NeuralNetworkError("To convert to flat, all non-input layers must have threshold values.");
			}
			
			lastActivation = layer.getActivationFunction();
		}
	}
}
