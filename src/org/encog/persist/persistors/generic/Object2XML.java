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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
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

	/**
	 * Save the object to XML.
	 * 
	 * @param obj
	 *            The object to save.
	 * @param out
	 *            The XML writer.
	 */
	public void save(final EncogPersistedObject obj, final WriteXML out) {

		try {
			PersistorUtil.beginEncogObject(obj.getClass().getSimpleName(), out,
					obj, true);

			for (final Field field : obj.getClass().getDeclaredFields()) {
				field.setAccessible(true);

				if (field.getName().equalsIgnoreCase("name")
						|| field.getName().equalsIgnoreCase("description")) {
					continue;
				}

				if ((field.getModifiers() & Modifier.FINAL) == 0) {
					final Object value = field.get(obj);
					if (value != null) {
						if (value instanceof Collection) {
							out.beginTag(field.getName());
							saveCollection(out, (Collection<?>) value);
							out.endTag();
						} else {
							out.addProperty(field.getName(), value.toString());
						}
					}
				}
			}

			out.endTag();
		} catch (final IllegalAccessException e) {
			throw new PersistError(e);
		}

	}

	/**
	 * Save a collection.
	 * 
	 * @param out
	 *            The XML writer.
	 * @param value
	 *            The value to save.
	 */
	private void saveCollection(final WriteXML out, final Collection<?> value) {

		for (final Object obj : value) {
			if (obj instanceof String) {
				out.addProperty("S", obj.toString());
			}
		}
	}
}
