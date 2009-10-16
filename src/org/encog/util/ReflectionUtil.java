package org.encog.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.encog.persist.annotations.EGIgnore;

public class ReflectionUtil {
	
	public static Collection<Field> getAllFields(Class<?> c)
	{
		List<Field> result = new ArrayList<Field>();
		getAllFields(c,result);
		return result;
	}
	
	public static void getAllFields(Class<?> c,Collection<Field> fields)
	{
		for(Field field: c.getDeclaredFields())
		{
			fields.add(field);
		}
		
		Class<?> s = c.getSuperclass();
		if(s!=null)
			getAllFields(s,fields);
	}
	
	public static boolean isSimple(Object obj)
	{
		return (obj instanceof File) || (obj instanceof String);
	}

	public static boolean shouldAccessField(Field field, boolean base) {
		if (field.getAnnotation(EGIgnore.class) != null)			
			return false;

		if ((field.getModifiers() & Modifier.STATIC) == 0) {
			if (base) {
				if (field.getName().equalsIgnoreCase("name")
						|| field.getName().equalsIgnoreCase("description"))
					return false;
			}
			return true;
		}
		return false;
	}
	
	public static boolean isPrimitive(Object obj) {
		return (obj instanceof Character) || (obj instanceof Integer)
				|| (obj instanceof Short) || (obj instanceof Float)
				|| (obj instanceof Double) || (obj instanceof Boolean);
	}
	
	
}
