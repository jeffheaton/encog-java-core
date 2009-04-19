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
package org.encog.neural.prune;

import java.util.Collection;

import org.encog.matrix.Matrix;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Prune {
	
	private BasicNetwork network;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public Prune(BasicNetwork network)
	{
		this.network = network;
	}
	
	public void changeNeuronCount(Layer layer, int neuronCount)
	{
		// is there anything to do?
		if( neuronCount==layer.getNeuronCount())
			return;
		
		if( neuronCount>layer.getNeuronCount() )
			increaseNeuronCount(layer,neuronCount);
		else
			decreaseNeuronCount(layer,neuronCount);
	}
	
	private void increaseNeuronCount(Layer layer,int neuronCount)
	{
		// adjust the threshold
		double[] newThreshold = new double[neuronCount];
		for(int i=0;i<layer.getNeuronCount();i++)
		{
			newThreshold[i] = layer.getThreshold(i);
		}
		
		layer.setThreshold(newThreshold);
		
		// adjust the outbound weight matrixes
		for(Synapse synapse: layer.getNext())
		{
			Matrix newMatrix = new Matrix(neuronCount,synapse.getToNeuronCount());
			// copy existing matrix to new matrix
			for(int row = 0;row<layer.getNeuronCount();row++)
			{
				for(int col = 0;col<synapse.getToNeuronCount();col++)
				{
					newMatrix.set(row,col,synapse.getMatrix().get(row, col));
				}
			}
		}
			
		// adjust the inbound weight matrixes
		Collection<Synapse> inboundSynapses = this.network.getStructure().getPreviousSynapses(layer);
		
		for(Synapse synapse: inboundSynapses)
		{
			Matrix newMatrix = new Matrix(synapse.getFromNeuronCount(), neuronCount);
			// copy existing matrix to new matrix
			for(int row = 0;row<synapse.getFromNeuronCount();row++)
			{
				for(int col = 0;col<neuronCount;col++)
				{
					newMatrix.set(row,col,synapse.getMatrix().get(row, col));
				}
			}
		}
		
		// finally, up the neuron count
		layer.setNeuronCount(neuronCount);
	}
	
	private void decreaseNeuronCount(Layer layer,int neuronCount)
	{
		
	}

	public BasicNetwork getNetwork()
	{
		return this.network;
	}
	
	public void determineNeuronSignificance(int neuron)
	{
		
	}
	
	
}

