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

	/**
	 * The properties tag.
	 */
	public static final String TAG_PROPERTIES = "properties";
	
	/**
	 * The property tag.
	 */
	public static final String TAG_PROPERTY = "Property";
	
	/**
	 * The name attribute.
	 */
	public static final String ATTRIBUTE_NAME = "name";
	
	/**
	 * The value attribute.
	 */
	public static final String ATTRIBUTE_VALUE = "value";

	/**
	 * The property data being loaed.
	 */
	private PropertyData propertyData;

	/**
	 * Handle the properties tag.
	 * @param in the XML reader.
	 */
	private void handleProperties(final ReadXML in) {
		while (in.readToTag()) {
			if (in.is(PropertyDataPersistor.TAG_PROPERTY, true)) {
				handleProperty(in);
			} else if (in.is(PropertyDataPersistor.TAG_PROPERTIES, false)) {
				break;
			}

		}

	}

	/**
	 * Handle loading an individual property.
	 * @param in The XML reader.
	 */
	private void handleProperty(final ReadXML in) {
		final String name = in.getTag().getAttributeValue(
				PropertyDataPersistor.ATTRIBUTE_NAME);
		final String value = in.getTag().getAttributeValue(
				PropertyDataPersistor.ATTRIBUTE_VALUE);
		this.propertyData.set(name, value);
	}

	/**
	 * Load the specified Encog object from an XML reader.
	 * 
	 * @param in
	 *            The XML reader to use.
	 * @return The loaded object.
	 */
	public EncogPersistedObject load(final ReadXML in) {

		final String name = in.getTag().getAttributes().get(
				EncogPersistedCollection.ATTRIBUTE_NAME);
		final String description = in.getTag().getAttributes().get(
				EncogPersistedCollection.ATTRIBUTE_DESCRIPTION);

		this.propertyData = new PropertyData();

		this.propertyData.setName(name);
		this.propertyData.setDescription(description);

		while (in.readToTag()) {
			if (in.is(PropertyDataPersistor.TAG_PROPERTIES, true)) {
				handleProperties(in);
			} else if (in.is(EncogPersistedCollection.TYPE_PROPERTY, false)) {
				break;
			}

		}

		return this.propertyData;
	}

	/**
	 * Save the specified Encog object to an XML writer.
	 * 
	 * @param obj
	 *            The object to save.
	 * @param out
	 *            The XML writer to save to.
	 */
	public void save(final EncogPersistedObject obj, final WriteXML out) {

		final PropertyData pData = (PropertyData) obj;

		PersistorUtil.beginEncogObject(EncogPersistedCollection.TYPE_PROPERTY,
				out, obj, true);
		out.beginTag(PropertyDataPersistor.TAG_PROPERTIES);
		for (final String key : pData.getData().keySet()) {
			out.addAttribute(PropertyDataPersistor.ATTRIBUTE_NAME, key);
			out.addAttribute(PropertyDataPersistor.ATTRIBUTE_VALUE, pData
					.get(key));
			out.beginTag(PropertyDataPersistor.TAG_PROPERTY);
			out.endTag();
		}
		out.endTag();
		out.endTag();

	}

}
