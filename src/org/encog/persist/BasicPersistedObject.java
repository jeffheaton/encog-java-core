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

package org.encog.persist;

import org.encog.persist.persistors.generic.GenericPersistor;

/**
 * A basic Encog persisted object. Provides the name, description and collection
 * attributes. Also provides a generic persistor.
 */
public class BasicPersistedObject implements EncogPersistedObject {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the object.
	 */
	private String name;

	/**
	 * The description of the object.
	 */
	private String description;

	/**
	 * The collection the object belongs to.
	 */
	private EncogCollection collection;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Persistor createPersistor() {
		return new GenericPersistor(this.getClass());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EncogCollection getCollection() {
		return this.collection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCollection(final EncogCollection collection) {
		this.collection = collection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDescription(final String theDescription) {
		this.description = theDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(final String theName) {
		this.name = theName;

	}
}
