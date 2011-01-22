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
package org.encog.persist;

import java.io.Serializable;

import org.encog.persist.map.PersistedObject;

/**
 * This interface flags an class as being able to be persisted into an Encog
 * collection.
 * 
 * @author jheaton
 * 
 */
public interface EncogPersistedObject extends Serializable {

	/**
	 * Create a persistor for this object.
	 * 
	 * @return A persistor for this object.
	 */
	Persistor createPersistor();

	/**
	 * @return The description of this object.
	 */
	String getDescription();

	/**
	 * @return The name of this object.
	 */
	String getName();

	/**
	 * Set the description of this object.
	 * 
	 * @param theDescription
	 *            The description.
	 */
	void setDescription(String theDescription);

	/**
	 * Set the name of this object.
	 * 
	 * @param theName
	 *            The name of this object.
	 */
	void setName(String theName);

	EncogCollection getCollection();
	
	void setCollection(EncogCollection collection);
	
	public boolean supportsMapPersistence();
	
	public void persistToMap(PersistedObject obj);
	
	public void persistFromMap(PersistedObject obj);
	
}
