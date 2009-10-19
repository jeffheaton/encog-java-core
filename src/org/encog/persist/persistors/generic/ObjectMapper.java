package org.encog.persist.persistors.generic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;

public class ObjectMapper {

	private Map<Integer, Object> objectMap = new HashMap<Integer, Object>();
	private List<FieldMapping> list = new ArrayList<FieldMapping>();

	public void addFieldMapping(int ref, Field field, Object target) {
		this.list.add(new FieldMapping(ref, field, target));
	}

	public void addObjectMapping(int ref, Object obj) {
		this.objectMap.put(ref, obj);
	}

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

	public void clear() {
		this.objectMap.clear();
		this.list.clear();
	}
}
