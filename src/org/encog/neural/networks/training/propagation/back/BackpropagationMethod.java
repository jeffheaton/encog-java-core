package org.encog.neural.networks.training.propagation.back;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.propagation.PropagationLevel;
import org.encog.neural.networks.training.propagation.PropagationMethod;
import org.encog.neural.networks.training.propagation.PropagationSynapse;

public class BackpropagationMethod implements PropagationMethod {


	private double handleMatrixDelta(
			final NeuralOutputHolder outputHolder,
			final PropagationLevel fromLevel,
			final PropagationLevel toLevel,
			Layer toLayer,
			int toNeuron,
			PropagationSynapse fromSynapse,
			int fromNeuron)
	{
		NeuralData output = outputHolder.getResult().get(fromSynapse.getSynapse());
		fromSynapse.accumulateMatrixDelta(fromNeuron, toNeuron, toLevel.getDelta(toNeuron) * output.getData(fromNeuron));		
		return (fromSynapse.getSynapse().getMatrix().get(fromNeuron, toNeuron) * toLevel.getDelta(toNeuron) );
	}
	
	private void handleThresholdDelta(PropagationSynapse fromSynapse, int toNeuron, PropagationLevel toLevel)
	{
		fromSynapse.accumulateThresholdDelta(toNeuron, toLevel.getDelta(toNeuron) );
	}
	
	@Override
	public void calculateError(
			final NeuralOutputHolder output,
			final PropagationLevel fromLevel,
			final PropagationLevel toLevel) {
		
		// used to hold the errors from this level to the next
		double[] errors = new double[fromLevel.getNeuronCount()];
		
		// loop over every element of the weight matrix and determine the deltas
		// also determine the threshold deltas.
		for(Layer toLayer: toLevel.getLayers() )
		{
			for(int toNeuron=0;toNeuron<toLayer.getNeuronCount();toNeuron++)
			{
				int neuronIndex = 0;
				
				for(PropagationSynapse fromSynapse: fromLevel.getOutgoing() )
				{
					for(int fromNeuron=0; fromNeuron<fromSynapse.getSynapse().getFromNeuronCount(); fromNeuron++)
					{
						errors[neuronIndex++]+=handleMatrixDelta(
								output,
								fromLevel,
								toLevel,
								toLayer,
								toNeuron,
								fromSynapse,
								fromNeuron);
					}
					handleThresholdDelta(fromSynapse, toNeuron,toLevel);
				}				
			}				 
		}
		
		for(int i=0;i<fromLevel.getNeuronCount();i++)
		{
			double localActual = fromLevel.getActual(i);
			fromLevel.setDelta(i,localActual);
		}
		
		fromLevel.applyDerivative();
		
		for(int i=0;i<fromLevel.getNeuronCount();i++)
		{
			fromLevel.setDelta(i, fromLevel.getDelta(i)*errors[i]);
		}
		
	}


}
