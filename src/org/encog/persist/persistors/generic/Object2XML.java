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
package org.encog.persist.persistors.generic;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.persist.annotations.EGBackPointer;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.persistors.PersistorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic class used to take an object and produce XML for it. Some of the
 * Encog persistors make use of this class.
 * 
 * @author jheaton
 * 
 */
public class Object2XML {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private WriteXML out;

	/**
	 * Save the object to XML.
	 * 
	 * @param obj
	 *            The object to save.
	 * @param out
	 *            The XML writer.
	 */
	public void save(final EncogPersistedObject encogObject, final WriteXML out) {
		this.out = out;
		try {
			PersistorUtil.beginEncogObject(encogObject.getClass()
					.getSimpleName(), out, encogObject, true);

			for (final Field childField : encogObject.getClass()
					.getDeclaredFields()) {
				if (this.shouldAccessField(childField, true)) {
					childField.setAccessible(true);
					Object childValue = childField.get(encogObject);
					out.beginTag(childField.getName());
					saveField(childValue);
					out.endTag();
				}
			}

			out.endTag();
		} catch (final IllegalAccessException e) {
			throw new PersistError(e);
		}

	}

	private void saveObject(Object parentObject)
			throws IllegalArgumentException, IllegalAccessException {
		out.beginTag(parentObject.getClass().getSimpleName());
		for (final Field childField : parentObject.getClass()
				.getDeclaredFields()) {
			childField.setAccessible(true);
			if (shouldAccessField(childField, false)) {
				Object childValue = childField.get(parentObject);
				out.beginTag(childField.getName());
				saveField(childValue);
				out.endTag();
			}
		}
		out.endTag();
	}

	private void saveField(Object fieldObject) throws IllegalArgumentException,
			IllegalAccessException {
		if (fieldObject != null) {
			if (fieldObject instanceof Collection) {

				saveCollection(out, (Collection<?>) fieldObject);

			} else if (isPrimitive(fieldObject) || isSimple(fieldObject) ) {
				out.addText(fieldObject.toString());
			} else if (fieldObject instanceof String) {

			} else {
				saveObject(fieldObject);
			}
		}
	}

	private boolean isPrimitive(Object obj) {
		return (obj instanceof Character) || (obj instanceof Integer)
				|| (obj instanceof Short) || (obj instanceof Float)
				|| (obj instanceof Double) || (obj instanceof Boolean);
	}
	
	private boolean isSimple(Object obj)
	{
		return (obj instanceof File) || (obj instanceof String);
	}

	private boolean shouldAccessField(Field field, boolean base) {
		if (field.getAnnotation(EGIgnore.class) != null)			
			return false;
		
		if (field.getAnnotation(EGBackPointer.class) != null)
			return false;

		if ((field.getModifiers() & Modifier.STATIC) == 0) {
			if (base) {
				if (field.getName().equalsIgnoreCase("name")
						|| field.getName().equalsIgnoreCase("description"))
					return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Save a collection.
	 * 
	 * @param out
	 *            The XML writer.
	 * @param value
	 *            The value to save.
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private void saveCollection(final WriteXML out, final Collection<?> value)
			throws IllegalArgumentException, IllegalAccessException {

		for (final Object obj : value) {
			this.saveObject(obj);
		}
	}
}
