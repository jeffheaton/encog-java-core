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

package org.encog.neural.networks.training.propagation.resilient;

import org.encog.matrix.Matrix;
import org.encog.neural.networks.NeuralOutputHolder;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.propagation.CalculatePartialDerivative;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.PropagationLevel;
import org.encog.neural.networks.training.propagation.PropagationMethod;
import org.encog.neural.networks.training.propagation.PropagationSynapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResilientPropagationMethod implements PropagationMethod {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ResilientPropagation propagation;
	private CalculatePartialDerivative pderv = new CalculatePartialDerivative();
	
	public void calculateError(
			final NeuralOutputHolder output,
			final PropagationLevel fromLevel,
			final PropagationLevel toLevel) {
		
		this.pderv.calculateError(output, fromLevel, toLevel);
		
	}

	public void init(Propagation propagation) {
		this.propagation = (ResilientPropagation)propagation;
		
	}

	/**
	 * Modify the weight matrix and thresholds based on the last call to
	 * calcError.
	 */
	public void learn() {
		if (logger.isDebugEnabled()) {
			logger.debug("Backpropagation learning pass");
		}
		
		for(PropagationLevel level: this.propagation.getLevels())
		{
			learnLevel(level);
		}
	}
	
	private int sign(double value)
	{
		if( Math.abs(value)<this.propagation.getZeroTolerance())
			return 0;
		else if( value>0)
			return 1;
		else 
			return -1;
	}
	
	private void learnLevel(PropagationLevel level)
	{
		// teach the synapses
		for(PropagationSynapse synapse: level.getOutgoing())
		{
			learnSynapse(synapse);
		}		
		
		// teach the threshold
		for(Layer layer: level.getLayers())
		{
			for(int i=0;i<layer.getNeuronCount();i++)
			{
				//double change = sign(level.getThresholdGradient(i)*this.propagation.getLearningRate());
				//layer.setThreshold(i, 0);
			
				// multiply the current and previous gradient, and take the sign.  We want to see if the gradient has changed its sign.
				int change = sign(level.getThresholdGradient(i)*level.getLastThresholdGradent(i));
				double weightChange = 0;
				
				// if the gradient has retained its sign, then we increase the delta so that it will converge faster
				if( change>0 )
				{
					double delta = level.getThresholdDelta(i) * ResilientPropagation.POSITIVE_ETA;
					delta = Math.min(delta, this.propagation.getMaxStep());
					weightChange = sign(level.getThresholdGradient(i)) * delta;
					level.setThresholdDelta(i, delta);
					level.setLastThresholdGradient(i, level.getThresholdGradient(i));
				}
				// if change<0, then the sign has changed, and the last delta was too big
				else if( change<0 )
				{
					double delta = level.getThresholdDelta(i) * ResilientPropagation.NEGATIVE_ETA;
					delta = Math.max(delta, ResilientPropagation.DELTA_MIN);
					level.setThresholdDelta(i, delta);
					// set the previous gradient to zero so that there will be no adjustment the next iteration
					level.setLastThresholdGradient(i, 0);	
				}
				// if change==0 then there is no change to the delta
				else if( change==0 )
				{
					double delta = level.getThresholdDelta(i);
					weightChange = sign(level.getThresholdGradient(i)) * delta;
					level.setLastThresholdGradient(i, level.getThresholdGradient(i));
				}
				
				// apply the weight change, if any
				layer.setThreshold(i, layer.getThreshold(i)+weightChange);
			}			
		}
	}
	
	/**
	 * Learn from the last error calculation.
	 * 
	 * @param learnRate
	 *            The learning rate.
	 * @param momentum
	 *            The momentum.
	 */
	private void learnSynapse(PropagationSynapse synapse) {

		Matrix matrix = synapse.getSynapse().getMatrix();
		
		for(int row = 0;row<matrix.getRows();row++ )
		{
			for(int col = 0;col<matrix.getCols();col++ )
			{
				// multiply the current and previous gradient, and take the sign.  We want to see if the gradient has changed its sign.
				int change = sign(synapse.getAccMatrixGradients().get(row, col)*synapse.getLastMatrixGradients().get(row,col));
				double weightChange = 0;
				
				// if the gradient has retained its sign, then we increase the delta so that it will converge faster
				if( change>0 )
				{
					double delta = synapse.getDeltas().get(row, col) * ResilientPropagation.POSITIVE_ETA;
					delta = Math.min(delta, this.propagation.getMaxStep());
					weightChange = sign(synapse.getAccMatrixGradients().get(row, col)) * delta;
					synapse.getDeltas().set(row,col,delta);
					synapse.getLastMatrixGradients().set(row,col,synapse.getAccMatrixGradients().get(row, col));
				}
				// if change<0, then the sign has changed, and the last delta was too big
				else if( change<0 )
				{
					double delta = synapse.getDeltas().get(row, col) * ResilientPropagation.NEGATIVE_ETA;
					delta = Math.max(delta, ResilientPropagation.DELTA_MIN);
					synapse.getDeltas().set(row,col,delta);
					// set the previous gradent to zero so that there will be no adjustment the next iteration
					synapse.getLastMatrixGradients().set(row,col,0);	
				}
				// if change==0 then there is no change to the delta, th
				else if( change==0 )
				{
					double delta = synapse.getDeltas().get(row, col);
					weightChange = sign(synapse.getAccMatrixGradients().get(row, col)) * delta;
					synapse.getLastMatrixGradients().set(row,col,synapse.getAccMatrixGradients().get(row, col));
				}
				
				// apply the weight change, if any
				matrix.set(row,col,synapse.getSynapse().getMatrix().get(row, col)+weightChange);
				
				
			}
		}	
		
		// clear out the gradient accumulator for the next iteration
		synapse.getAccMatrixGradients().clear();
	}


}
