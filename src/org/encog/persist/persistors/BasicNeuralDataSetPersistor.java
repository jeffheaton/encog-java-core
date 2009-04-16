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

package org.encog.persist.persistors;

import java.util.Map;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.parse.tags.Tag.Type;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.util.ReadCSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Encog persistor used to persist the ActivationBiPolar class.
 * 
 * @author jheaton
 */
public class BasicNeuralDataSetPersistor implements Persistor {

	public final static String TAG_ITEM = "Item";
	public final static String TAG_INPUT = "Input";
	public final static String TAG_IDEAL = "Ideal";
	
	private BasicNeuralDataSet currentDataSet;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	private void handleItem(final ReadXML in)
	{
		Map<String,String> properties = in.readPropertyBlock();
		NeuralDataPair pair = null;
		NeuralData input = new BasicNeuralData(ReadCSV.fromCommas(properties.get(TAG_INPUT)));
		
		if( properties.containsKey(TAG_IDEAL))
		{
			// supervised			
			NeuralData ideal = new BasicNeuralData(ReadCSV.fromCommas(properties.get(TAG_IDEAL)));
			pair = new BasicNeuralDataPair(input,ideal);
		}
		else
		{
			// unsupervised
			pair = new BasicNeuralDataPair(input);			
		}
		
		this.currentDataSet.add(pair);
	}

	public EncogPersistedObject load(ReadXML in) {
				
		String name = in.getTag().getAttributes().get(EncogPersistedCollection.ATTRIBUTE_NAME);
		String description = in.getTag().getAttributes().get(EncogPersistedCollection.ATTRIBUTE_DESCRIPTION);
			
		this.currentDataSet = new BasicNeuralDataSet();
		currentDataSet.setName(name);
		currentDataSet.setDescription(description);
		
		while( in.readToTag() )
		{
			if( in.is(TAG_ITEM,true) )
			{
				handleItem(in);
			}
			else if( in.is(TAG_ITEM,false) )
			{
				break;
			}
			
		}
		
		return this.currentDataSet;
	}
		
	public void save(EncogPersistedObject obj, WriteXML out) {
		PersistorUtil.beginEncogObject(EncogPersistedCollection.TYPE_TRAINING, out, obj, true);
		NeuralDataSet set = (NeuralDataSet)obj;
		StringBuilder builder = new StringBuilder();
		
		for(NeuralDataPair pair: set)
		{
			out.beginTag(TAG_ITEM);
			
			ReadCSV.toCommas(builder,pair.getInput().getData());
			out.addProperty(TAG_INPUT, builder.toString());
			
			if( pair.getIdeal()!=null )
			{
				ReadCSV.toCommas(builder,pair.getIdeal().getData());
				out.addProperty(TAG_IDEAL, builder.toString());
			}
			out.endTag();
		}
		out.endTag();
		
	}

}
