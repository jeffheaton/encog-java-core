/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
