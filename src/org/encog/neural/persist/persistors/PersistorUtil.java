package org.encog.neural.persist.persistors;

import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.PersistError;
import org.encog.neural.persist.Persistor;
import org.encog.util.xml.XMLWrite;

public class PersistorUtil {
	/**
	 * Create a persistor object. These objects know how to persist certain
	 * types of classes.
	 * 
	 * @param className
	 *            The name of the class to create a persistor for.
	 * @return The persistor for the specified class.
	 */
	public static Persistor createPersistor(final String className) {
		try {
			String name = className + "Persistor";
			final Class<?> c = Class
					.forName("org.encog.neural.persist.persistors." + name);
			final Persistor persistor = (Persistor) c.newInstance();
			return persistor;
		} catch (final ClassNotFoundException e) {
			return null;
		} catch (final InstantiationException e) {
			return null;
		} catch (final IllegalAccessException e) {
			return null;
		}
	}
	
	public static void beginEncogObject(
			String objectType,
			XMLWrite out,
			EncogPersistedObject obj)
	{
		if( obj.getName()==null )
		{
			throw new PersistError("Encog object must have a name to be saved.");
		}
		out.addAttribute("name", obj.getName());
		out.addAttribute("native", obj.getClass().getName());
		if( obj.getDescription()==null )
			obj.setDescription("");
		out.addAttribute("description", obj.getDescription());
		out.beginTag(objectType);
	}
}
