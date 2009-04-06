/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.neural.networks.training.propagation;

import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculatePartialDerivative {
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
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
				
				toLevel.setThresholdGradient(toNeuronGlobal, toLevel.getThresholdGradient(toNeuronGlobal)+toLevel.getDelta(toNeuronGlobal));
				toNeuronGlobal++;
			}				 
		}
		
		for(int i=0;i<fromLevel.getNeuronCount();i++)
		{
			double actual = fromLevel.getActual(i);
			fromLevel.setDelta(i,actual);
		}
		
		// get an activation function to use
		Layer l = toLevel.getLayers().get(0);
		l.getActivationFunction().derivativeFunction(fromLevel.getDeltas());
		
		
		for(int i=0;i<fromLevel.getNeuronCount();i++)
		{
			fromLevel.setDelta(i, fromLevel.getDelta(i)*errors[i]);
		}
		
	}
}
