package org.encog.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	
	
}
