/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;

/**
 * Used to map objects to reference numbers. This is where reference numbers are
 * resolved. This class is used by Encog generic persistence.
 */
public class ObjectMapper {

	/**
	 * A map from reference numbers to objects.
	 */
	private final Map<Integer, Object> objectMap = 
		new HashMap<Integer, Object>();

	/**
	 * A list of all of the field mappings.
	 */
	private final List<FieldMapping> list = new ArrayList<FieldMapping>();

	/**
	 * Add a field mapping to be resolved later. This builds a list of
	 * references to be resolved later when the resolve method is called.
	 * 
	 * @param ref
	 *            The reference number.
	 * @param field
	 *            The field to map.
	 * @param target
	 *            The target object that holds the field.
	 */
	public void addFieldMapping(final int ref, final Field field,
			final Object target) {
		this.list.add(new FieldMapping(ref, field, target));
	}

	/**
	 * Add an object mapping to be resolved later.
	 * 
	 * @param ref
	 *            The object reference.
	 * @param obj
	 *            The object.
	 */
	public void addObjectMapping(final int ref, final Object obj) {
		this.objectMap.put(ref, obj);
	}

	/**
	 * Clear the map and reference list.
	 */
	public void clear() {
		this.objectMap.clear();
		this.list.clear();
	}

	/**
	 * Resolve all references and place the correct objects.
	 */
	public void resolve() {
		try {
			for (final FieldMapping field : this.list) {
				final Object obj = this.objectMap.get(field.getRef());
				field.getField().setAccessible(true);
				field.getField().set(field.getTarget(), obj);
			}
		} catch (final IllegalArgumentException e) {
			throw new EncogError(e);
		} catch (final IllegalAccessException e) {
			throw new EncogError(e);
		}
	}
}
