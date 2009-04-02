/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */

package org.encog.neural.persist.persistors;

import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.PersistError;
import org.encog.neural.persist.Persistor;
import org.encog.util.xml.XMLWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistorUtil {
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
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
			// handle any hard coded ones
			if( className.equals("TrainingData"))
				return new BasicNeuralDataSetPersistor();
			
			// find using classes
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
