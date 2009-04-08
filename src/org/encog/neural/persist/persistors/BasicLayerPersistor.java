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

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.ReadCSV;
import org.encog.util.xml.XMLElement;
import org.encog.util.xml.XMLRead;
import org.encog.util.xml.XMLWrite;

public class BasicLayerPersistor implements Persistor {

	public static final String TAG_BASIC_LAYER = "BasicLayer";
	public static final String TAG_ACTIVATION = "activation";
	public static final String PROPERTY_NEURONS = "neurons";
	public static final String PROPERTY_THRESHOLD = "threshold";
	
	public EncogPersistedObject load(XMLElement node, XMLRead in) {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(EncogPersistedObject obj, XMLWrite out) {
		PersistorUtil.beginEncogObject(TAG_BASIC_LAYER, out, obj, false);
		BasicLayer layer = (BasicLayer)obj;
		out.addProperty(PROPERTY_NEURONS, layer.getNeuronCount());
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
