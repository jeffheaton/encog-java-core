package org.encog.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.normalize.output.OutputField;
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.location.ResourcePersistence;

public class ReflectionUtil {

	private static Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();

	public static void loadClassmap() {
		try {
			ResourcePersistence resource = new ResourcePersistence(
					"org/encog/data/classes.txt");
			InputStream is = resource.createInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				Class<?> c = Class.forName(line);
				ReflectionUtil.classMap.put(c.getSimpleName(), c);
			}
			is.close();
		} catch (IOException e) {
			throw new EncogError(e);
		} catch (ClassNotFoundException e) {
			throw new EncogError(e);
		}

	}

	public static Class<?> resolveEncogClass(String name) {
		if( ReflectionUtil.classMap.size()==0)
			loadClassmap();
		return ReflectionUtil.classMap.get(name);
	}

	public static Collection<Field> getAllFields(Class<?> c) {
		List<Field> result = new ArrayList<Field>();
		getAllFields(c, result);
		return result;
	}

	public static void getAllFields(Class<?> c, Collection<Field> fields) {
		for (Field field : c.getDeclaredFields()) {
			fields.add(field);
		}

		Class<?> s = c.getSuperclass();
		if (s != null)
			getAllFields(s, fields);
	}

	public static boolean isSimple(Object obj) {
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
	
	public static Field findField(Class<?> c, String name)
	{
		Collection<Field> list = getAllFields(c);
		for(Field field:list)
		{
			if( field.getName().equals(name))
			{
				field.setAccessible(true);
				return field;
			}
		}
		return null;
	}

	public static boolean isInstanceOf(Class<?> class1,
			Class<?> class2) {
		
		// same class?
		if( class1.equals(class2) )
			return true;

		// implements interface
		for( Class<?> clazz: class1.getInterfaces() )
		{
			if( clazz.equals(class1))
				return true;
		}
		
		if( class1.getSuperclass()==null )
			return false;
		else
			return isInstanceOf(class1.getSuperclass(),class2);
	}

}
