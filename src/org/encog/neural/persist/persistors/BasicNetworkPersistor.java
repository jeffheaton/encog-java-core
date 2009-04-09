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

package org.encog.neural.persist.persistors;

import java.util.HashMap;
import java.util.Map;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.parse.tags.Tag.Type;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;

public class BasicNetworkPersistor implements Persistor {
	

	public static final String TAG_BASIC_NETWORK = "BasicNetwork";
	public static final String TAG_LAYERS = "layers";
	public static final String TAG_SYNAPSES = "synapses";
	public static final String TAG_SYNAPSE = "synapse";
	public static final String TAG_LAYER = "layer";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_TYPE_INPUT = "input";
	public static final String ATTRIBUTE_TYPE_OUTPUT = "output";
	public static final String ATTRIBUTE_TYPE_HIDDEN = "hidden";
	public static final String ATTRIBUTE_TYPE_UNKNOWN = "unknown";
	public static final String ATTRIBUTE_FROM = "from";
	public static final String ATTRIBUTE_TO = "to";
	
	private BasicNetwork currentNetwork;
	private final Map<Layer , Integer> layer2index = new HashMap<Layer , Integer>();
		
	private void saveLayers(WriteXML out)
	{
		int current = 1;
		for(Layer layer: currentNetwork.getStructure().getLayers())
		{
			String type;
			
			if(this.currentNetwork.isInput(layer))
			{
				type = BasicNetworkPersistor.ATTRIBUTE_TYPE_INPUT;
			}
			else if(this.currentNetwork.isOutput(layer))
			{
				type = BasicNetworkPersistor.ATTRIBUTE_TYPE_OUTPUT;
			}
			else if(this.currentNetwork.isHidden(layer))
			{
				type = BasicNetworkPersistor.ATTRIBUTE_TYPE_HIDDEN;
			}
			else
				type = BasicNetworkPersistor.ATTRIBUTE_TYPE_UNKNOWN;
			
			out.addAttribute(ATTRIBUTE_ID, ""+current);
			out.addAttribute(ATTRIBUTE_TYPE, type);
			out.beginTag(TAG_LAYER);
			Persistor persistor = layer.createPersistor();
			persistor.save(layer, out);
			out.endTag();
			this.layer2index.put(layer, current);
			current++;
		}
	}
	
	private void saveSynapses(WriteXML out)
	{
		for(Synapse synapse: this.currentNetwork.getStructure().getSynapses())
		{
			out.addAttribute(ATTRIBUTE_FROM, ""+this.layer2index.get(synapse.getFromLayer()));
			out.addAttribute(ATTRIBUTE_TO, ""+this.layer2index.get(synapse.getToLayer()));
			out.beginTag(TAG_SYNAPSE);
			Persistor persistor = synapse.createPersistor();
			persistor.save(synapse, out);
			out.endTag();
		}
	}


	public void save(EncogPersistedObject obj, WriteXML out) {
		PersistorUtil.beginEncogObject(TAG_BASIC_NETWORK, out, obj, true);
		this.currentNetwork = (BasicNetwork)obj;
		
		this.currentNetwork.getStructure().finalizeStructure();
		
		// save the layers
		out.beginTag(TAG_LAYERS);
		saveLayers(out);
		out.endTag();
		
		// save the structure of these layers
		out.beginTag(TAG_SYNAPSES);		
		saveSynapses(out);
		out.endTag();
		out.endTag();
	}
	

	public EncogPersistedObject load(ReadXML in) {
				
		this.currentNetwork = new BasicNetwork();
		
		while( in.readToTag() ) 
		{
			if( in.is(TAG_LAYERS,true))
			{
				handleLayers(in);
			} 
			else if( in.is(TAG_SYNAPSES,true))
			{
				handleSynapses(in);
			}
				
		}
		
		return this.currentNetwork;
	}
	
	private void handleLayers(ReadXML in)
	{		
		while( in.readToTag() )  
		{
			if( in.is(TAG_LAYER,true) )
			{
				in.readToTag();
				Persistor persistor = PersistorUtil.createPersistor(in.getTag().getName());
				Layer layer = (Layer) persistor.load(in);
				System.out.println(layer);
			}
		}
	}
	
	private void handleSynapses(ReadXML in)
	{
		
	}
}
