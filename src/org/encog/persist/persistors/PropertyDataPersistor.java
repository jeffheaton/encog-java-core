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

import org.encog.neural.data.PropertyData;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

/**
 * The Encog persistor used to persist the PropertyData class.
 * 
 * @author jheaton
 */
public class PropertyDataPersistor implements Persistor {

	public final static String TAG_PROPERTIES = "properties";
	public final static String TAG_PROPERTY = "Property";
	public final static String ATTRIBUTE_NAME = "name";
	public final static String ATTRIBUTE_VALUE = "value";
	
	private PropertyData propertyData;
	
	public EncogPersistedObject load(ReadXML in) {
		
		String name = in.getTag().getAttributes().get(EncogPersistedCollection.ATTRIBUTE_NAME);
		String description = in.getTag().getAttributes().get(EncogPersistedCollection.ATTRIBUTE_DESCRIPTION);
		
		propertyData = new PropertyData();
		
		propertyData.setName(name);
		propertyData.setDescription(description);
		
		while( in.readToTag() )
		{
			if( in.is(TAG_PROPERTIES,true) )
			{
				handleProperties(in);
			}
			else if( in.is(EncogPersistedCollection.TYPE_PROPERTY,false) )
			{
				break;
			}
			
		}
		
		return propertyData;
	}

	private void handleProperties(ReadXML in) {
		while( in.readToTag() )
		{
			if( in.is(TAG_PROPERTY,true) )
			{
				handleProperty(in);
			}
			else if( in.is(TAG_PROPERTIES,false) )
			{
				break;
			}
			
		}
		
	}

	private void handleProperty(ReadXML in) {
		String name = in.getTag().getAttributeValue(ATTRIBUTE_NAME);
		String value = in.getTag().getAttributeValue(ATTRIBUTE_VALUE);
		this.propertyData.set(name, value);
	}

	public void save(EncogPersistedObject obj, WriteXML out) {
		
		PropertyData pData = (PropertyData)obj;
		
		PersistorUtil.beginEncogObject(EncogPersistedCollection.TYPE_PROPERTY, out, obj, true);
		out.beginTag(TAG_PROPERTIES);
		for(String key: pData.getData().keySet())
		{
			out.addAttribute(ATTRIBUTE_NAME,key);
			out.addAttribute(ATTRIBUTE_VALUE, pData.get(key));
			out.beginTag(TAG_PROPERTY);
			out.endTag();
		}
		out.endTag();
		out.endTag();
		
	}

}
