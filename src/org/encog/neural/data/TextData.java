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

import org.encog.persist.BasicPersistedObject;
import org.encog.persist.EncogCollection;
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
public class TextData extends BasicPersistedObject {
	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 6895724776252007263L;

	/**
	 * The text data that is stored.
	 */
	private String text;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Clone this object.
	 * 
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
	 * 
	 * @return A persistor.
	 */
	public Persistor createPersistor() {
		return new TextDataPersistor();
	}

	/**
	 * @return The text held by this object.
	 */
	public String getText() {
		return this.text;
	}


	/**
	 * Set the text held by this object.
	 * 
	 * @param text
	 *            The text held by this object.
	 */
	public void setText(final String text) {
		this.text = text;
	}
}
