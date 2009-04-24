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

import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.TextDataPersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Encog object that can hold text data. This object can be stored in an
 * Encog persisted file.
 * 
 * @author jheaton
 * 
 */
public class TextData implements EncogPersistedObject {
	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 6895724776252007263L;

	/**
	 * The text data that is stored.
	 */
	private String text;

	/**
	 * The name of this object.
	 */
	private String name;
	
	/**
	 * The description of this object.
	 */
	private String description;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Clone this object.
	 * @return A cloned version of this object.
	 */
	@Override
	public Object clone() {
		final TextData result = new TextData();
		result.setName(getName());
		result.setDescription(getDescription());
		result.setText(getText());
		return result;
	}

	/**
	 * Create a persistor to store this object.
	 * @return A persistor.
	 */
	public Persistor createPersistor() {
		return new TextDataPersistor();
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
	 * @return The text held by this object.
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Set the description of this object.
	 * @param description The description of this object.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Set the name of this object.
	 * @param name The name of this object.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Set the text held by this object.
	 * @param text The text held by this object.
	 */
	public void setText(final String text) {
		this.text = text;
	}

}
