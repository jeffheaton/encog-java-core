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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds a level worth of information used by each of the propagation 
 * methods.  A level is defined as all of the layers that feed a single
 * next layer.  In a pure feedforward neural network there will be
 * only one layer per level.  However, recurrent neural networks will
 * contain multiple layers per level.
 * @author jheaton
 *
 */
public class PropagationLevel {
	
	private final int neuronCount;
	private final List<Layer> layers = new ArrayList<Layer>();
	private final List<PropagationSynapse> outgoing = new ArrayList<PropagationSynapse>();
	private final double[] deltas;
	private final double[] thresholdGradients;
	private final double[] lastThresholdGradients;
	private final double[] thresholdDeltas;
	private final Propagation propagation;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public PropagationLevel(Propagation propagation, Layer layer)
	{
		this.neuronCount = layer.getNeuronCount();
		this.deltas = new double[this.neuronCount];
		this.thresholdGradients = new double[this.neuronCount];
		this.lastThresholdGradients = new double[this.neuronCount];
		this.layers.add(layer);		
		this.propagation = propagation;
		this.thresholdDeltas = new double[this.neuronCount];
	}
	
	public PropagationLevel(Propagation propagation, List<Synapse> outgoing)
	{
		int count = 0;
		
		this.propagation = propagation;
		this.outgoing.clear();
		
		Set<Layer> layerSet = new HashSet<Layer>();
		
		for(Synapse synapse: outgoing)
		{
			count+=synapse.getFromNeuronCount();
			layerSet.add(synapse.getFromLayer());
			PropagationSynapse propSynapse = new PropagationSynapse(synapse);
			this.outgoing.add(propSynapse);
		}
		
		this.layers.addAll(layerSet);
		
		this.neuronCount = count;
		
		this.deltas = new double[this.neuronCount];
		this.thresholdGradients = new double[this.neuronCount];
		this.lastThresholdGradients = new double[this.neuronCount];
		this.thresholdDeltas = new double[this.neuronCount];
	}

	public int getNeuronCount() {
		return neuronCount;
	}

	public List<PropagationSynapse> getOutgoing() {
		return outgoing;
	}

	public void setDelta(int index, double d) {
		this.deltas[index] = d;		
	}
	
	public double getDelta(int index)
	{
		return this.deltas[index];
	}
	
	public double[] getDeltas()
	{
		return this.deltas;
	}

	public List<Layer> getLayers() {
		return layers;
	}
	
	public List<Synapse> determinePreviousSynapses()
	{
		List<Synapse> result = new ArrayList<Synapse>();
		
		for(Layer layer: this.layers)
		{
			Collection<Synapse> synapses = this.propagation.getNetwork().getStructure().getPreviousSynapses(layer);
			
			// add all teachable synapses
			for(Synapse synapse: synapses)
			{
				if(synapse.isTeachable())
					result.add(synapse);
			}
		}
			
		return result;
		
	}
	

	public double getActual(final int index)
	{
		int currentIndex = index;
		
		// is this the output layer, if so then we need to return the output from
		// the entire network.
		if( this.outgoing.size()==0 )
		{
			NeuralData actual = this.propagation.getOutputHolder().getOutput();
			return actual.getData(index);
		}
		
		// not the output layer, so we need output from one of the previous layers.
		for(PropagationSynapse synapse: this.outgoing )
		{
			int count = synapse.getSynapse().getFromNeuronCount();
			
			if( currentIndex<count )
			{
				NeuralData actual = this.propagation.getOutputHolder().getResult().get(synapse.getSynapse());
				return actual.getData(currentIndex);
			}
			
			currentIndex-=count;
		}
		
		throw new NeuralNetworkError("Could not find actual value while propagation training.");
	}
	
	public double[] getThresholdGradients()
	{
		return this.thresholdGradients;
	}
	
	public double getThresholdGradient(int index)
	{
		return this.thresholdGradients[index];
	}
	
	public void setThresholdGradient(int index,double d)
	{
		this.thresholdGradients[index] = d;
	}
	
	public double getLastThresholdGradent(int index)
	{
		return this.lastThresholdGradients[index];
	}
	
	public void accumulateThresholdGradient(final int index, final double value) {
		this.thresholdGradients[index]+= value;
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[PropagationLevel(");
		result.append(this.neuronCount);
		result.append("):");
		for(Layer layer: this.layers)
		{
			result.append(layer.toString());
		}
		result.append("]");
		return result.toString();
	}

	public void setLastThresholdGradient(int i, double delta) {
		this.lastThresholdGradients[i] = delta;
		
	}
	
	public double getThresholdDelta(int i)
	{
		return this.thresholdDeltas[i];
	}
	
	public void setThresholdDelta(int i,double d)
	{
		this.thresholdDeltas[i] = d;
	}
	
	
}
