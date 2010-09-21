/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.neural.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.encog.EncogError;
import org.encog.persist.BasicPersistedObject;
import org.encog.persist.EncogCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.PropertyDataPersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Encog data object that can be used to hold property data. This is a
 * collection of name-value pairs that can be saved in an Encog persisted file.
 * 
 * @author jheaton
 * 
 */
public class PropertyData extends BasicPersistedObject {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -7940416732740995199L;

	/**
	 * The property data.
	 */
	private final Map<String, String> data = new HashMap<String, String>();

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Clone this object.
	 * 
	 * @return A clonned version of this object.
	 */
	@Override
	public Object clone() {
		final PropertyData result = new PropertyData();
		result.setName(getName());
		result.setDescription(getDescription());
		result.setCollection(getCollection());

		for (final String key : this.data.keySet()) {
			result.set(key, get(key));
		}
		return result;
	}

	/**
	 * @return A persistor for the property data.
	 */
	public Persistor createPersistor() {
		return new PropertyDataPersistor();
	}

	/**
	 * Get the specified property.
	 * 
	 * @param name
	 *            The property name.
	 * @return The property value.
	 */
	public String get(final String name) {
		return this.data.get(name);
	}

	/**
	 * Get all of the property data as a map.
	 * 
	 * @return The property data.
	 */
	public Map<String, String> getData() {
		return this.data;
	}

	/**
	 * Get a property as a date.
	 * 
	 * @param field
	 *            The name of the field.
	 * @return The date value.
	 */
	public Date getDate(final String field) {
		try {
			final String str = get(field);
			final DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
			final Date date = formatter.parse(str);
			return date;
		} catch (final ParseException e) {
			throw new EncogError(e);
		}
	}

	/**
	 * Get a property as a double.
	 * 
	 * @param field
	 *            The name of the field.
	 * @return The double value.
	 */
	public double getDouble(final String field) {
		final String str = get(field);
		try {
			return Double.parseDouble(str);
		} catch (final NumberFormatException e) {
			throw new EncogError(e);
		}
	}

	/**
	 * Get a property as an integer.
	 * 
	 * @param field
	 *            The name of the field.
	 * @return The integer value.
	 */
	public int getInteger(final String field) {
		final String str = get(field);
		try {
			return Integer.parseInt(str);
		} catch (final NumberFormatException e) {
			throw new EncogError(e);
		}
	}

	/**
	 * Determine of the specified property is defined.
	 * 
	 * @param key
	 *            The property to check.
	 * @return True if this property is defined.
	 */
	public boolean isDefined(final String key) {
		return this.data.containsKey(key);
	}

	/**
	 * Remove the specified property.
	 * 
	 * @param key
	 *            The property to remove.
	 */
	public void remove(final String key) {
		this.data.remove(key);
	}

	/**
	 * Set the specified property.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param value
	 *            The value to set the property to.
	 */
	public void set(final String name, final String value) {
		this.data.put(name, value);
	}

	/**
	 * @return The number of properties defined.
	 */
	public int size() {
		return this.data.size();
	}
}
