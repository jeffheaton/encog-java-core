/*
 * Encog(tm) Core v2.4
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

import java.lang.reflect.Field;

/**
 * A simple mapping that holds the reference, field and target of an object.
 * This is used internally by the object mapper to help resolve references.
 * 
 */
public class FieldMapping {

	/**
	 * The field's reference.
	 */
	private final int ref;

	/**
	 * The field object.
	 */
	private final Field field;

	/**
	 * The target object, that holds the field.
	 */
	private final Object target;

	/**
	 * Construct a field mapping.
	 * 
	 * @param ref
	 *            The field reference.
	 * @param field
	 *            The field.
	 * @param target
	 *            The target that holds the field.
	 */
	public FieldMapping(final int ref, final Field field, final Object target) {
		this.ref = ref;
		this.field = field;
		this.target = target;
	}

	/**
	 * @return The field.
	 */
	public Field getField() {
		return this.field;
	}

	/**
	 * @return The field reference.
	 */
	public int getRef() {
		return this.ref;
	}

	/**
	 * @return The target object that holds the field.
	 */
	public Object getTarget() {
		return this.target;
	}

}
