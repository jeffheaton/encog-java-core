package org.encog.persist.persistors.generic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;

/**
 * Used to map objects to reference numbers. This is where reference numbers are
 * resolved. This class is used by Encog generic persistance.
 */
public class ObjectMapper {

	/**
	 * A map from reference numbers to objects.
	 */
	private Map<Integer, Object> objectMap = new HashMap<Integer, Object>();

	/**
	 * A list of all of the field mappings.
	 */
	private List<FieldMapping> list = new ArrayList<FieldMapping>();

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
	public void addFieldMapping(int ref, Field field, Object target) {
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
	public void addObjectMapping(int ref, Object obj) {
		this.objectMap.put(ref, obj);
	}

	/**
	 * Resolve all references and place the correct objects.
	 */
	public void resolve() {
		try {
			for (FieldMapping field : list) {
				Object obj = this.objectMap.get(field.getRef());
				field.getField().setAccessible(true);
				field.getField().set(field.getTarget(), obj);
			}
		} catch (IllegalArgumentException e) {
			throw new EncogError(e);
		} catch (IllegalAccessException e) {
			throw new EncogError(e);
		}
	}

	/**
	 * Clear the map and reference list.
	 */
	public void clear() {
		this.objectMap.clear();
		this.list.clear();
	}
}
