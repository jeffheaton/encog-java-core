/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
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
package org.encog.persist.persistors.generic;

import org.encog.persist.EncogCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

/**
 * Simple object to persist with the generic class.
 * 
 * @author jheaton
 */
public class SimpleObjectToPersist implements EncogPersistedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3193782478666198020L;

	/**
	 * The first test string.
	 */
	private String first;

	/**
	 * The second test string.
	 */
	private String second;

	/**
	 * A test number.
	 */
	private double number;

	/**
	 * The name of this object.
	 */
	private String name;

	/**
	 * The description of this object.
	 */
	private String description;

	/**
	 * Not used for this simple test.
	 * 
	 * @return Not used.
	 */
	@Override
	public Object clone() {
		return null;
	}

	/**
	 * Not used for this simple test.
	 * 
	 * @return Not used for this simple test.
	 */
	public Persistor createPersistor() {
		return null;
	}

	/**
	 * @return The description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return The first test string.
	 */
	public String getFirst() {
		return this.first;
	}

	/**
	 * @return The name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The second test string.
	 */
	public double getNumber() {
		return this.number;
	}

	/**
	 * @return The second test string.
	 */
	public String getSecond() {
		return this.second;
	}

	/**
	 * Set the description of this object.
	 * 
	 * @param theDescription
	 *            The description of this object.
	 */
	public void setDescription(final String theDescription) {
		this.description = theDescription;
	}

	/**
	 * Set the first test string.
	 * 
	 * @param first
	 *            The new value.
	 */
	public void setFirst(final String first) {
		this.first = first;
	}

	/**
	 * Set the name of this object.
	 * 
	 * @param theName
	 *            The name of this object.
	 */
	public void setName(final String theName) {
		this.name = theName;
	}

	/**
	 * The number to test with.
	 * 
	 * @param number
	 *            The new value.
	 */
	public void setNumber(final double number) {
		this.number = number;
	}

	/**
	 * The second test string.
	 * 
	 * @param second
	 *            The new value.
	 */
	public void setSecond(final String second) {
		this.second = second;
	}

	public EncogCollection getCollection() {
		return null;
	}


	public void setCollection(EncogCollection collection) {
		
	}

}
