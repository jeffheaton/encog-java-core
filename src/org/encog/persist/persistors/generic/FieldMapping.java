package org.encog.persist.persistors.generic;

import java.lang.reflect.Field;

public class FieldMapping {
	private int ref;
	private final Field field; 
	private final Object target;
	
	public FieldMapping(int ref, Field field, Object target) {
		this.ref = ref;
		this.field = field;
		this.target = target;
	}
	
	public int getRef()
	{
		return ref;
	}

	public Field getField() {
		return field;
	}

	public Object getTarget() {
		return target;
	}
	
	
}
