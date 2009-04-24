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
package org.encog.neural.data;

import java.util.HashMap;
import java.util.Map;

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
public class PropertyData implements EncogPersistedObject {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -7940416732740995199L;

	/**
	 * The name.
	 */
	private String name;

	/**
	 * The description.
	 */
	private String description;

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
	 * @return A clonned version of this object.
	 */
	@Override
	public Object clone() {
		final PropertyData result = new PropertyData();
		result.setName(getName());
		result.setDescription(getDescription());

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
	 * @param name The property name.
	 * @return The property value.
	 */
	public String get(final String name) {
		return this.data.get(name);
	}

	/**
	 * Get all of the property data as a map.
	 * @return The property data.
	 */
	public Map<String, String> getData() {
		return this.data;
	}

	/**
	 * @return The description of this object.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return The name of this object.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Determine of the specified property is defined.
	 * @param key The property to check.
	 * @return True if this property is defined.
	 */
	public boolean isDefined(final String key) {
		return this.data.containsKey(key);
	}

	/**
	 * Remove the specified property.
	 * @param key The property to remove.
	 */
	public void remove(final String key) {
		this.data.remove(key);
	}

	/**
	 * Set the specified property.
	 * @param name The name of the property.
	 * @param value The value to set the property to.
	 */
	public void set(final String name, final String value) {
		this.data.put(name, value);
	}

	/**
	 * Set the description for this object.
	 * @param description The description of this property.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Set the name of this property.
	 * @param name The name of this property.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return The number of properties defined.
	 */
	public int size() {
		return this.data.size();
	}
}
