/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
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
	 * 
	 * @param in
	 *            the XML reader.
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
	 * 
	 * @param in
	 *            The XML reader.
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
