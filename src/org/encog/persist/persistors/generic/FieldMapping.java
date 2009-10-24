package org.encog.persist.persistors.generic;

import java.lang.reflect.Field;

/**
 * A simple mapping that holds the reference, field and target of an object.  This is used internally
 * by the object mapper to help resolve references.
 *
 */
public class FieldMapping {
	
	/**
	 * The field's reference.
	 */
	private int ref;
	
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
	 * @param ref The field reference.
	 * @param field The field.
	 * @param target The target that holds the field.
	 */
	public FieldMapping(int ref, Field field, Object target) {
		this.ref = ref;
		this.field = field;
		this.target = target;
	}
	
	/**
	 * @return The field reference.
	 */
	public int getRef()
	{
		return ref;
	}

	/**
	 * @return The field.
	 */
	public Field getField() {
		return field;
	}

	/**
	 * @return The target object that holds the field.
	 */
	public Object getTarget() {
		return target;
	}
	
	
}
