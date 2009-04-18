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

package org.encog.neural.networks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds "cached" information about the structure of the neural network.
 * This is a very good performance boost since the neural network does not
 * need to traverse itself each time a complete collection of layers or
 * synapses is needed.
 * @author jheaton
 *
 */
public class NeuralStructure {
	private List<Layer> layers = new ArrayList<Layer>();
	private List<Synapse> synapses = new ArrayList<Synapse>();
	private BasicNetwork network;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public NeuralStructure(BasicNetwork network)
	{
		this.network = network;
	}
	
	public void finalizeStructure()
	{
		finalizeLayers();
		finalizeSynapses();		
	}
	
	private void finalizeLayers()
	{
		Set<Layer> result = new HashSet<Layer>();
		if( this.network.getInputLayer()!=null)
			getLayers(result, this.network.getInputLayer());
		this.layers.clear();
		this.layers.addAll(result);
	}
	
	private void finalizeSynapses()
	{
		Set<Synapse> result = new HashSet<Synapse>();
		for (Layer layer : getLayers()) {
			for (Synapse synapse : layer.getNext()) {
				result.add(synapse);
			}
		}
		this.synapses.clear();
		this.synapses.addAll(result);
	}
	
	private void getLayers(Set<Layer> result, Layer layer) {
		result.add(layer);

		for (Synapse synapse : layer.getNext()) {
			Layer nextLayer = synapse.getToLayer();

			if (!result.contains(nextLayer)) {
				getLayers(result, nextLayer);
			}
		}
	}

	public Collection<Layer> getPreviousLayers(Layer targetLayer) {
		Collection<Layer> result = new HashSet<Layer>();
		for (Layer layer : this.getLayers()) {
			for (Synapse synapse : layer.getNext()) {
				if (synapse.getToLayer() == targetLayer) {
					result.add(synapse.getFromLayer());
				}
			}
		}
		return result;
	}

	public Collection<Synapse> getPreviousSynapses(Layer targetLayer) {
		
		Collection<Synapse> result = new HashSet<Synapse>();
		
		for(Synapse synapse: this.synapses)
		{
			if( synapse.getToLayer()==targetLayer)
				result.add(synapse);
		}
		
		return result;
		
	}

	public List<Layer> getLayers() {
		return layers;
	}

	public List<Synapse> getSynapses() {
		return synapses;
	}

	public BasicNetwork getNetwork() {
		return network;
	}
}
