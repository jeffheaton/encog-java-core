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

package org.encog.persist.persistors.generic;

import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.persist.Persistor;

/**
 * An Encog perisistor that can be used with any object that supports the Encog
 * generic persistence. Simply provide the class to the constructor, and return
 * an instance of this object in the getPersistor call.
 * 
 * When loading an object, Encog will attempt to use this class if no other
 * suitable persistor can be found.
 */
public class GenericPersistor implements Persistor {

	/**
	 * The class that this persistor is used with.
	 */
	private final Class< ? > clazz;

	/**
	 * Construct a generic persistor for the specified class.
	 * 
	 * @param clazz
	 *            The class to construct a persistor for.
	 */
	public GenericPersistor(final Class< ? > clazz) {
		this.clazz = clazz;
	}

	/**
	 * Load from the specified node.
	 * 
	 * @param in
	 *            The node to load from.
	 * @return The EncogPersistedObject that was loaded.
	 */
	public EncogPersistedObject load(final ReadXML in) {
		EncogPersistedObject current;
		try {
			current = (EncogPersistedObject) this.clazz.newInstance();
			final XML2Object conv = new XML2Object();
			conv.load(in, current);
			return current;
		} catch (final InstantiationException e) {
			throw new PersistError(e);
		} catch (final IllegalAccessException e) {
			throw new PersistError(e);
		}

	}

	/**
	 * Save the specified object.
	 * 
	 * @param obj
	 *            The object to save.
	 * @param out
	 *            The XML object.
	 */
	public void save(final EncogPersistedObject obj, final WriteXML out) {
		final Object2XML conv = new Object2XML();
		conv.save(obj, out);
	}

}
