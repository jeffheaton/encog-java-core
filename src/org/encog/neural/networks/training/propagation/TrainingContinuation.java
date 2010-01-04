/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
package org.encog.neural.networks.training.propagation;

import java.util.HashMap;
import java.util.Map;

import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.TrainingContinuationPersistor;

/**
 * Allows training to be continued.
 * 
 */
public class TrainingContinuation implements EncogPersistedObject {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = -3649790586015301015L;
	
	/**
	 * The name of this object.
	 */
	private String name;
	
	/**
	 * The description of this object.
	 */
	private String description;
	
	/**
	 * The contents of this object.
	 */
	private final Map<String, Object> contents = new HashMap<String, Object>();

	/**
	 * @return A persistor for this object.
	 */
	public Persistor createPersistor() {
		return new TrainingContinuationPersistor();
	}

	/**
	 * Get an object by name.
	 * @param name The name of the object.
	 * @return The object requested.
	 */
	public Object get(final String name) {
		return this.contents.get(name);
	}

	/**
	 * @return The contents.
	 */
	public Map<String, Object> getContents() {
		return this.contents;
	}

	/**
	 * @return The description.
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
	 * Save a list of doubles.
	 * @param key The key to save them under.
	 * @param list The list of doubles.
	 */
	public void put(final String key, final double[] list) {
		this.contents.put(key, list);
	}

	/**
	 * Set a value to a string.
	 * @param name The value to set.
	 * @param value The value.
	 */
	public void set(final String name, final Object value) {
		this.contents.put(name, value);
	}

	/**
	 * Set the description.
	 * @param description The description.
	 */
	public void setDescription(final String description) {
		this.description = description;

	}

	/**
	 * Set the name of this object.
	 * @param name The name.
	 */
	public void setName(final String name) {
		this.name = name;
	}

}
