/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */
package org.encog.persist;

import java.util.Collection;
import java.util.List;

import org.encog.persist.location.PersistenceLocation;

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
