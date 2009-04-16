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

import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.util.ReadCSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides basic functions that many of the persistors will need.
 * 
 * 
 * @author jheaton
 */
public class BasicLayerPersistor implements Persistor {

	
	public static final String TAG_ACTIVATION = "activation";
	public static final String PROPERTY_NEURONS = "neurons";
	public static final String PROPERTY_THRESHOLD = "threshold";
	public static final String PROPERTY_X = "x";
	public static final String PROPERTY_Y = "y";
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public EncogPersistedObject load(ReadXML in) {
		
		int neuronCount = 0;
		int x=0;
		int y=0;
		String threshold = null;
		ActivationFunction activation = null;
		String end = in.getTag().getName();
		
		while( in.readToTag() ) 
		{
			if( in.is(TAG_ACTIVATION,true))
			{
				in.readToTag();
				String type = in.getTag().getName();
				Persistor persistor = PersistorUtil.createPersistor(type);
				activation = (ActivationFunction) persistor.load(in);				
			}
			else if( in.is(PROPERTY_NEURONS,true))
			{
				neuronCount = in.readIntToTag();
			}
			else if( in.is(PROPERTY_THRESHOLD,true))
			{
				threshold = in.readTextToTag();
			}
			else if( in.is(PROPERTY_X,true))
			{
				x = in.readIntToTag();
			}
			else if( in.is(PROPERTY_Y,true))
			{
				y = in.readIntToTag();
			}
			else if( in.is(end, false))
				break;
		}
		
		if( neuronCount>0)
		{			
			BasicLayer layer; 
			
			if( threshold==null )
			{
				layer = new BasicLayer(activation,false,neuronCount);
			}
			else
			{
				double[] t = ReadCSV.fromCommas(threshold);
				layer = new BasicLayer(activation,true,neuronCount);
				for(int i=0;i<t.length;i++)
				{
					layer.setThreshold(i, t[i]);
				}
			}
			layer.setX(x);
			layer.setY(y);
			return layer;
		}
		return null;
	}

	public void save(EncogPersistedObject obj, WriteXML out) {
		PersistorUtil.beginEncogObject(EncogPersistedCollection.TYPE_BASIC_LAYER, out, obj, false);
		BasicLayer layer = (BasicLayer)obj;
		
		out.addProperty(PROPERTY_NEURONS, layer.getNeuronCount());
		out.addProperty(PROPERTY_X, layer.getX());
		out.addProperty(PROPERTY_Y, layer.getY());
		
		if( layer.hasThreshold())
		{
			StringBuilder result = new StringBuilder();
			ReadCSV.toCommas(result, layer.getThreshold());
			out.addProperty(PROPERTY_THRESHOLD,result.toString() );
		}
		
		out.beginTag(TAG_ACTIVATION);
		Persistor persistor = layer.getActivationFunction().createPersistor();
		persistor.save(layer.getActivationFunction(), out);
		out.endTag();
				
		out.endTag();		
	}

}
