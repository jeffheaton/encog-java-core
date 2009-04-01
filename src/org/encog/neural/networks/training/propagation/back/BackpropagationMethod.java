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
			int toNeuronLocal,
			PropagationSynapse fromSynapse,
			int fromNeuron,
			int toNeuronGlobal)
	{
		NeuralData output = outputHolder.getResult().get(fromSynapse.getSynapse());
		fromSynapse.accumulateMatrixDelta(fromNeuron, toNeuronLocal, toLevel.getDelta(toNeuronGlobal) * output.getData(fromNeuron));		
		return (fromSynapse.getSynapse().getMatrix().get(fromNeuron, toNeuronLocal) * toLevel.getDelta(toNeuronGlobal) );
	}
	
	@Override
	public void calculateError(
			final NeuralOutputHolder output,
			final PropagationLevel fromLevel,
			final PropagationLevel toLevel) {
		
		// used to hold the errors from this level to the next
		double[] errors = new double[fromLevel.getNeuronCount()];
		
		int toNeuronGlobal = 0;		
		
		// loop over every element of the weight matrix and determine the deltas
		// also determine the threshold deltas.		
		for(Layer toLayer: toLevel.getLayers() )
		{
			for(int toNeuron=0;toNeuron<toLayer.getNeuronCount();toNeuron++)
			{
				int fromNeuronGlobal = 0;
				
				for(PropagationSynapse fromSynapse: fromLevel.getOutgoing() )
				{
					for(int fromNeuron=0; fromNeuron<fromSynapse.getSynapse().getFromNeuronCount(); fromNeuron++)
					{
						errors[fromNeuronGlobal++]+=handleMatrixDelta(
								output,
								fromLevel,
								toLevel,
								toLayer,
								toNeuron,
								fromSynapse,
								fromNeuron,
								toNeuronGlobal);
					}
					
				}	
				
				toLevel.setThresholdDelta(toNeuronGlobal, toLevel.getThresholdDelta(toNeuronGlobal)+toLevel.getDelta(toNeuronGlobal));
				toNeuronGlobal++;
			}				 
		}
		
		for(int i=0;i<fromLevel.getNeuronCount();i++)
		{
			double actual = fromLevel.getActual(i);
			fromLevel.setDelta(i,actual);
		}
		
		fromLevel.applyDerivative();
		
		for(int i=0;i<fromLevel.getNeuronCount();i++)
		{
			fromLevel.setDelta(i, fromLevel.getDelta(i)*errors[i]);
		}
		
	}


}
