package org.encog.persist.persistors.generic;

import org.encog.normalize.Normalization;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.persist.Persistor;

public class GenericPersistor implements Persistor {

	private Class<?> clazz;
	
	public GenericPersistor(Class<?> clazz)
	{
		this.clazz = clazz;
	}
	
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

	public void save(EncogPersistedObject obj, WriteXML out) {	
		Object2XML conv = new Object2XML();
		conv.save(obj, out);		
	}

}
