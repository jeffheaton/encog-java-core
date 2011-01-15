/*
 * Encog(tm) Core v2.6 - Java Version
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

import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.persist.Persistor;
import org.encog.persist.map.PersistedObject;

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
			if( current.supportsMapPersistence() ) {
				XML2Map conv = new XML2Map();
				PersistedObject po = conv.load(in);
				current.persistFromMap(po);
				return current;
			} else {
				final XML2Object conv = new XML2Object();
				conv.load(in, current);
				return current;
			}
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
		if( obj.supportsMapPersistence() ) {
			PersistedObject po = new PersistedObject();
			obj.persistToMap(po);
			Map2XML conv = new Map2XML();
			conv.save(po,out);
		} else {		
			final Object2XML conv = new Object2XML();
			conv.save(obj, out);
		}
	}

}
