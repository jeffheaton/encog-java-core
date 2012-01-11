/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.util.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.util.file.ResourceInputStream;

/**
 * This class includes some utilities to be used with reflection. This are
 * mostly used by the Encog generic persistence classes.
 */
public final class ReflectionUtil {

	/**
	 * A map between short class names and the full path names.
	 */
	private static Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();

	/**
	 * Find the specified field, look also in superclasses.
	 * 
	 * @param c
	 *            The class to search.
	 * @param name
	 *            The name of the field we are looking for.
	 * @return The field.
	 */
	public static Field findField(final Class<?> c, final String name) {
		final Collection<Field> list = ReflectionUtil.getAllFields(c);
		for (final Field field : list) {
			if (field.getName().equals(name)) {
				field.setAccessible(true);
				return field;
			}
		}

		if (c.getSuperclass() != null) {
			return findField(c.getSuperclass(), name);
		}

		return null;
	}

	/**
	 * Get all of the fields from the specified class as a collection.
	 * 
	 * @param c
	 *            The class to access.
	 * @return All of the fields from this class and subclasses.
	 */
	public static Collection<Field> getAllFields(final Class<?> c) {
		final List<Field> result = new ArrayList<Field>();
		ReflectionUtil.getAllFields(c, result);
		return result;
	}

	/**
	 * Get all of the fields in the specified class and super classes.
	 * 
	 * @param c
	 *            The class to check.
	 * @param fields
	 *            A collection to hold the classes.
	 */
	public static void getAllFields(final Class<?> c,
			final Collection<Field> fields) {
		for (final Field field : c.getDeclaredFields()) {
			fields.add(field);
		}

		final Class<?> s = c.getSuperclass();
		if (s != null) {
			ReflectionUtil.getAllFields(s, fields);
		}
	}

	/**
	 * Determine if one class is an instance of the other class.
	 * 
	 * @param class1
	 *            The class to check.
	 * @param class2
	 *            Is class1 an instance of class 2.
	 * @return True if class 1 is an instance of class 2.
	 */
	public static boolean isInstanceOf(final Class<?> class1,
			final Class<?> class2) {

		// same class?
		if (class1.equals(class2)) {
			return true;
		}

		// implements interface
		for (final Class<?> clazz : class1.getInterfaces()) {
			if (clazz.equals(class1)) {
				return true;
			}
		}

		if (class1.getSuperclass() == null) {
			return false;
		} else {
			return ReflectionUtil.isInstanceOf(class1.getSuperclass(), class2);
		}
	}

	/**
	 * Determine if the specified object is a primitive.
	 * 
	 * @param obj
	 *            The object to check.
	 * @return True if this object is primitive.
	 */
	public static boolean isPrimitive(final Object obj) {
		return (obj instanceof Character) || (obj instanceof Integer)
				|| (obj instanceof Short) || (obj instanceof Float)
				|| (obj instanceof Double) || (obj instanceof Boolean)
				|| (obj instanceof Long);
	}

	/**
	 * Determine if an object is "simple", that is it should be persisted just
	 * with a .tostring.
	 * 
	 * @param obj
	 *            The object to check.
	 * @return True if the object is simple.
	 */
	public static boolean isSimple(final Object obj) {
		return (obj instanceof File) || (obj instanceof String);
	}

	/**
	 * Load the classmap file. This allows classes to be resolved using just the
	 * simple name.
	 */
	public static void loadStandardClassmap() {
		String currentClass = null;
		try {
			InputStream is = ResourceInputStream.openResourceInputStream("org/encog/data/classes.txt");
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() != 0) {
					currentClass = line;
					final Class<?> c = Class.forName(line);
					ReflectionUtil.classMap.put(c.getSimpleName(), c);
				}
			}
			is.close();
		} catch (final IOException e) {
			throw new EncogError(e);
		} catch (final ClassNotFoundException e) {
			throw new EncogError("Unknown class: " + currentClass);
		}

	}

	/**
	 * Load the classmap file. This allows classes to be resolved using just the
	 * simple name.
	 */
	public static void loadClassmap() {
		String currentClass = null;
		try {
			InputStream is = ResourceInputStream.openResourceInputStream("org/encog/data/classes.txt");
			
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() != 0) {
					currentClass = line;
					final Class<?> c = Class.forName(line);
					ReflectionUtil.classMap.put(c.getSimpleName(), c);
				}
			}
			is.close();
		} catch (final IOException e) {
			throw new EncogError(e);
		} catch (final ClassNotFoundException e) {
			throw new EncogError("Unknown class: " + currentClass);
		}
	}

	/**
	 * Resolve an encog class using its simple name.
	 * 
	 * @param name
	 *            The simple name of the class.
	 * @return The class requested.
	 */
	public static Class<?> resolveEncogClass(final String name) {
		synchronized (ReflectionUtil.classMap) {
			if (ReflectionUtil.classMap.size() == 0) {
				ReflectionUtil.loadClassmap();
			}
		}
		return ReflectionUtil.classMap.get(name);
	}

	/**
	 * Private constructor.
	 */
	private ReflectionUtil() {

	}

	/**
	 * Resolve an enumeration.
	 * @param field The field to resolve.
	 * @param value The value to get the enum for.
	 * @return The enum that was resolved.
	 */
	public static Object resolveEnum(final Field field, final String value) {
		final Class<?> type = field.getType();
		Object[] objs = type.getEnumConstants();
		for (Object obj : objs) {
			if (obj.toString().equals(value)) {
				return obj;
			}
		}
		return null;
	}

	/**
	 * Generate a hash code for an object.  Return 0 for null objects.
	 * @param <T> The type of object to generate for.
	 * @param o The object to generate.
	 * @return The hash code.
	 */
	public static <T> int safeHashCode(final T o) {
		if (o == null) {
			return 0;
		} else {
			return o.hashCode();
		}
	}
}
