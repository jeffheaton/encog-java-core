package org.encog.persist.persistors.generic;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.persist.annotations.EGReferenceable;
import org.encog.util.ReflectionUtil;

public class ObjectTagger {

	private Map<Object, Integer> map = new HashMap<Object, Integer>();
	private int currentID = 1;
	private int depth;
	
	public void clear() {
		this.map.clear();
		this.currentID = 1;
	}

	public void analyze(EncogPersistedObject encogObject)
	{
		try {
			depth = 0;
			tagObject(encogObject);
			for (final Field childField : ReflectionUtil.getAllFields(encogObject.getClass())) {
				if (ReflectionUtil.shouldAccessField(childField, true)) {
					childField.setAccessible(true);
					Object childValue = childField.get(encogObject);
					saveField(childValue);
				}
			}
		} catch (final IllegalAccessException e) {
			throw new PersistError(e);
		}		
	}

	private void saveField(Object fieldObject) throws IllegalArgumentException,
			IllegalAccessException {
		depth++;
		
		if( this.map.containsKey(fieldObject) )
			return;
		if (fieldObject != null) {
			if (fieldObject instanceof Collection) {
				saveCollection((Collection<?>) fieldObject);
			} else {
				saveObject(fieldObject);
			}
		}		
		depth--;
	}

	private void saveCollection(final Collection<?> value)
			throws IllegalArgumentException, IllegalAccessException {

		for (final Object obj : value) {
			saveObject(obj);
		}
	}

	private void saveObject(Object parentObject)
			throws IllegalArgumentException, IllegalAccessException {

		Collection<Field> allFields = ReflectionUtil.getAllFields(parentObject
				.getClass());
		
		tagObject(parentObject);

		// handle actual fields
		for (final Field childField : allFields) {
			childField.setAccessible(true);
			if (ReflectionUtil.shouldAccessField(childField, false)) {

				Object childValue = childField.get(parentObject);
				
				if( !ReflectionUtil.isPrimitive(childValue) && !ReflectionUtil.isSimple(childValue) )
				{
				if (depth > 50) {
						throw new PersistError(
								"Encog persistence is greater than 50 levels deep, closed loop likely.  Consider adding @EGReference tag near attribute: "
										+ parentObject.getClass().toString());
					}
					
					if (!this.map.containsKey(childValue))
						saveField(childValue);
				}
			}
		}
	}
	
	private void tagObject(Object obj) {
		if( obj.getClass().getAnnotation(EGReferenceable.class)!=null) {
			this.map.put(obj, this.currentID);
			this.currentID++;
		}
	}

	public int getReference(Object obj) {
		if( obj==null )
			return -1;
		return this.map.get(obj);
	}

	public boolean hasReference(Object obj) {		
		return this.map.containsKey(obj);
	}


}
