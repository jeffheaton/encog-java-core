package org.encog.persist.persistors.generic;

import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.persist.Persistor;

/**
 * An Encog perisistor that can be used with any object that supports the Encog
 * generic persistance. Simply provide the class to the constructor, and return
 * an instance of this object in the getPersistor call.
 * 
 * When loading an object, Encog will attempt to use this class if no other
 * suitable persistor can be found.
 */
public class GenericPersistor implements Persistor {

	/**
	 * The class that this persistor is used with.
	 */
	private Class<?> clazz;

	/**
	 * Construct a generic persistor for the specified class.
	 * @param clazz The class to construct a persistor for.
	 */
	public GenericPersistor(Class<?> clazz) {
		this.clazz = clazz;
	}

	/**
	 * Load from the specified node.
	 * 
	 * @param in
	 *            The node to load from.
	 * @return The EncogPersistedObject that was loaded.
	 */
	public EncogPersistedObject load(ReadXML in) {
		EncogPersistedObject current;
		try {
			current = (EncogPersistedObject) clazz.newInstance();
			XML2Object conv = new XML2Object();
			conv.load(in, current);
			return current;
		} catch (InstantiationException e) {
			throw new PersistError(e);
		} catch (IllegalAccessException e) {
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
	public void save(EncogPersistedObject obj, WriteXML out) {
		Object2XML conv = new Object2XML();
		conv.save(obj, out);
	}

}
