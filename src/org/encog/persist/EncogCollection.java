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

import java.util.Collection;

import org.encog.persist.location.PersistenceLocation;

/**
 * An interface to either a memory or disk-based encog collection.
 *
 */
public interface EncogCollection {
	/**
	 * Add an element to the collection.
	 * 
	 * @param name
	 *            The name of the element being added.
	 * @param obj
	 *            The object to be added.
	 */
	void add(final String name, final EncogPersistedObject obj);


	/**
	 * Build the directory. This allows the contents of the collection to be
	 * listed.
	 */
	void buildDirectory();

	/**
	 * Clear all elements from the collection.
	 */
	void clear();

	/**
	 * Delete an object from the collection.
	 * 
	 * @param object
	 *            The object to be deleted.
	 */
	void delete(final DirectoryEntry object);


	/**
	 * Delete a key from the collection.
	 * 
	 * @param key
	 *            The key to delete.
	 */
	void delete(final String key);

	/**
	 * Determine if the specified key exists.
	 * 
	 * @param key
	 *            The key.
	 * @return True, if the key exists.
	 */
	boolean exists(final String key);

	/**
	 * Find the object that corresponds to the specified directory entry, return
	 * null, if not found.
	 * 
	 * @param entry
	 *            The entry to find.
	 * @return The object that was found, or null, if no object was found.
	 */
	EncogPersistedObject find(final DirectoryEntry entry);

	/**
	 * Find the object that corresponds to the specified key.
	 * 
	 * @param key
	 *            The key to search for.
	 * @return The object that corresponds to the specified key.
	 */
	EncogPersistedObject find(final String key);


	/**
	 * @return A list of all the objects in the collection, specified by
	 *         DirectoryEntry objects.
	 */
	Collection<DirectoryEntry> getDirectory();

	/**
	 * @return The version of Encog that this file was created with.
	 */
	String getEncogVersion();

	/**
	 * @return The Encog file version.
	 */
	int getFileVersion();

	/**
	 * @return The platform that this file was created on.
	 */
	String getPlatform();


	/**
	 * Update the properties of an element in the collection. This allows the
	 * element to be renamed, if needed.
	 * 
	 * @param name
	 *            The name of the object that is being updated.
	 * @param newName
	 *            The new name, can be the same as the old name, if the
	 *            description is to be updated only.
	 * @param desc
	 *            The new description.
	 */
	void updateProperties(final String name, final String newName,
			final String desc);
	
	/**
	 * @return The location this collection is stored at.
	 */
	PersistenceLocation getLocation();
	

}
