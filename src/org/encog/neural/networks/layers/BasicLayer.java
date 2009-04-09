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
package org.encog.neural.networks.layers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.synapse.DirectSynapse;
import org.encog.neural.networks.synapse.OneToOneSynapse;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.neural.networks.synapse.WeightedSynapse;
import org.encog.neural.networks.synapse.WeightlessSynapse;
import org.encog.neural.persist.DirectoryEntry;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.neural.persist.persistors.BasicLayerPersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic functionality that most of the neural layers require.
 * 
 * @author jheaton
 */
public class BasicLayer implements Layer, Serializable {
	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -5682296868750703898L;	
	
	private List<Synapse> next = new ArrayList<Synapse>();
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Which activation function to use for this layer.
	 */
	private ActivationFunction activationFunction;


	/**
	 * The description for this object.
	 */
	private String description;

	/**
	 * The name for this object.
	 */
	private String name;
	
	private int neuronCount;
	
	private double[] threshold;
	

	/**
	 * Construct this layer with a non-default threshold function.
	 * 
	 * @param thresholdFunction
	 *            The threshold function to use.
	 * @param neuronCount
	 *            How many neurons in this layer.
	 */
	public BasicLayer(final ActivationFunction thresholdFunction,
			final boolean hasThreshold,final int neuronCount) {
		this.neuronCount = neuronCount;
		this.setActivationFunction( thresholdFunction );
		if( hasThreshold )
			this.threshold = new double[neuronCount];
	}

	/**
	 * Construct this layer with a sigmoid threshold function.
	 * 
	 * @param neuronCount
	 *            How many neurons in this layer.
	 */
	public BasicLayer(final int neuronCount) {
		this(new ActivationTANH(), true, neuronCount);
	}

	/**
	 * Compute the outputs for this layer given the input pattern. The output is
	 * also stored in the fire instance variable.
	 * 
	 * @param pattern
	 *            The input pattern.
	 * @return The output from this layer.
	 */
	public NeuralData compute(final NeuralData pattern) {
		
		NeuralData result = pattern.clone();
		
		if( this.hasThreshold())
		{
			// apply the thresholds
			for(int i=0;i<this.threshold.length;i++)
			{
				pattern.setData(i, result.getData(i)+this.threshold[i]);
			}
		}
		
		// apply the activation function
		this.getActivationFunction().activationFunction(result.getData());
		
		return result;
	}

	/**
	 * Create a persistor for this layer.
	 * @return The new persistor.
	 */
	public Persistor createPersistor() {
		return new BasicLayerPersistor();
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the neuron count for this layer.
	 * 
	 * @return the neuronCount
	 */
	public int getNeuronCount() {
		return this.neuronCount;
	}


	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	
	/**
	 * @return The activation function for this layer.
	 */
	public ActivationFunction getActivationFunction() {
		return this.activationFunction;
	}

	/**
	 * Set the activation function for this layer.
	 * @param f The activation function.
	 */
	public void setActivationFunction(final ActivationFunction f) {
		this.activationFunction = f;
	}
	
	public Collection<Layer> getNextLayers()
	{
		Collection<Layer> result = new HashSet<Layer>();
		for(Synapse synapse: this.next)
		{
			result.add(synapse.getToLayer());
		}
		return result;
	}

	public List<Synapse> getNext() {
		return next;
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		result.append("[");
		result.append(this.getClass().getSimpleName());
		result.append(": neuronCount=");
		result.append(this.neuronCount);
		result.append(']');
		return result.toString();
	}

	public void setNeuronCount(int neuronCount) {
		this.neuronCount = neuronCount;
	}
	
	public boolean isSelfConnected()
	{
		for(Synapse synapse: this.next)
		{
			if( synapse.isSelfConnected() )
				return true;
		}
		return false;
	}
	
	public void addNext(Layer next) {
		addNext(next, SynapseType.Weighted);
	}

	public void addNext(Layer next, SynapseType type) {
		Synapse synapse = null;
		
		switch(type)
		{
			case OneToOne:
				synapse = new OneToOneSynapse(this,next);
				break;
			case Weighted:
				synapse = new WeightedSynapse(this,next);
				break;
			case Weightless:
				synapse = new WeightlessSynapse(this,next);
				break;
			case Direct:
				synapse = new DirectSynapse(this,next);
				break;
		}
		
		if( synapse == null )
		{
			String str = "Unknown synapse type.";
			if( logger.isErrorEnabled())
			{
				logger.error(str);
			}
			throw new NeuralNetworkError(str);
		}
		else
			this.next.add(synapse);
	}


	public NeuralData recur() {
		return null;
	}
	
	public Object clone()
	{
		BasicLayer result = new BasicLayer(
				(ActivationFunction)this.activationFunction.clone(),
				this.hasThreshold(),
				this.getNeuronCount());
		return result;
		
	}

	public void process(NeuralData pattern) {		
	}
	
	public double getThreshold(int index)
	{
		if( !this.hasThreshold() )
		{
			String str = "Attempting to access threshold on a thresholdless layer.";
			if( logger.isErrorEnabled())
			{				
				logger.error(str);
			}
			throw new NeuralNetworkError(str);
		}
		return this.threshold[index];
	}
	
	public void setThreshold(int index,double d)
	{
		if( !this.hasThreshold() )
		{
			String str = "Attempting to set threshold on a thresholdless layer.";
			if( logger.isErrorEnabled())
			{				
				logger.error(str);
			}
			throw new NeuralNetworkError(str);
		}
		this.threshold[index] = d;
	}

	public double[] getThreshold()
	{
		return this.threshold;
	}
	
	public boolean hasThreshold()
	{
		return this.threshold!=null;
	}

	public void addSynapse(Synapse synapse) {
		this.next.add(synapse);
	}
	
}
