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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.persist.annotations.EGReferenceable;
import org.encog.util.ReflectionUtil;

/**
 * The object tagger is used in generic persistence to tag objects with a
 * reference number.
 * 
 */
public class ObjectTagger {

	/**
	 * The map of object to reference number.
	 */
	private final Map<Object, Integer> map = new HashMap<Object, Integer>();

	/**
	 * THe current reference ID.
	 */
	private int currentID = 1;

	/**
	 * The current depth.
	 */
	private int depth;

	/**
	 * Analyze the specified object and build a reference map.
	 * 
	 * @param encogObject
	 *            The object to analyze.
	 */
	public void analyze(final EncogPersistedObject encogObject) {
		try {
			this.depth = 0;
			assignObjectTag(encogObject);
			for (final Field childField : ReflectionUtil
					.getAllFields(encogObject.getClass())) {
				if (ReflectionUtil.shouldAccessField(childField, true)) {
					childField.setAccessible(true);
					final Object childValue = childField.get(encogObject);
					tagField(childValue);
				}
			}
		} catch (final IllegalAccessException e) {
			throw new PersistError(e);
		}
	}

	/**
	 * Assign a reference number to the specified object.
	 * @param obj The object to "tag".
	 */
	private void assignObjectTag(final Object obj) {
		if (obj.getClass().getAnnotation(EGReferenceable.class) != null) {
			this.map.put(obj, this.currentID);
			this.currentID++;
		}
	}

	/**
	 * Clear the map and current id.
	 */
	public void clear() {
		this.map.clear();
		this.currentID = 1;
	}

	/**
	 * Get the reference for the specified object.
	 * @param obj The object to check.
	 * @return -1 for no reference, otherwise the reference numebr.
	 */
	public int getReference(final Object obj) {
		if (obj == null) {
			return -1;
		}
		return this.map.get(obj);
	}

	/**
	 * Returns true if the object has a reference.
	 * @param obj The object to check.
	 * @return True if the object has a reference.
	 */
	public boolean hasReference(final Object obj) {
		return this.map.containsKey(obj);
	}

	/**
	 * Tag a collection, every object in the collection will be a reference.
	 * @param value The collection to tag.
	 * @throws IllegalAccessException An error.
	 */
	private void tagCollection(final Collection< ? > value)
			throws IllegalAccessException {

		for (final Object obj : value) {
			tagObject(obj);
		}
	}

	/**
	 * Tag a field.
	 * @param fieldObject The field to tag.
	 * @throws IllegalAccessException An error.
	 */
	private void tagField(final Object fieldObject)
			throws IllegalAccessException {
		this.depth++;

		if (this.map.containsKey(fieldObject)) {
			return;
		}
		if (fieldObject != null) {
			if (fieldObject instanceof Collection) {
				tagCollection((Collection< ? >) fieldObject);
			} else {
				tagObject(fieldObject);
			}
		}
		this.depth--;
	}

	/**
	 * Tan an object.
	 * @param parentObject The object to tag.
	 * @throws IllegalAccessException An error.
	 */
	private void tagObject(final Object parentObject)
			throws IllegalAccessException {

		final Collection<Field> allFields = ReflectionUtil
				.getAllFields(parentObject.getClass());

		assignObjectTag(parentObject);

		// handle actual fields
		for (final Field childField : allFields) {
			childField.setAccessible(true);
			if (ReflectionUtil.shouldAccessField(childField, false)) {

				final Object childValue = childField.get(parentObject);

				if (!ReflectionUtil.isPrimitive(childValue)
						&& !ReflectionUtil.isSimple(childValue)) {
					if (this.depth > 50) {
						throw new PersistError(
								"Encog persistence is greater than 50 levels deep, closed loop likely.  Consider adding @EGReference tag near attribute: "
										+ parentObject.getClass().toString());
					}

					if (!this.map.containsKey(childValue)) {
						tagField(childValue);
					}
				}
			}
		}
	}

}
