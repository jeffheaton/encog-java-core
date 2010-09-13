/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

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
import org.encog.persist.annotations.EGIgnore;
import org.encog.persist.location.ResourcePersistence;

/**
 * This class includes some utilities to be used with reflection. This are
 * mostly used by the Encog generic persistence classes.
 */
public final class ReflectionUtil {

	/**
	 * A map between short class names and the full path names.
	 */
	private static Map<String, Class< ? >> classMap = 
		new HashMap<String, Class< ? >>();

	/**
	 * Find the specified field, look also in superclasses.
	 * 
	 * @param c
	 *            The class to search.
	 * @param name
	 *            The name of the field we are looking for.
	 * @return The field.
	 */
	public static Field findField(final Class< ? > c, final String name) {
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
	public static Collection<Field> getAllFields(final Class< ? > c) {
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
	public static void getAllFields(final Class< ? > c,
			final Collection<Field> fields) {
		for (final Field field : c.getDeclaredFields()) {
			fields.add(field);
		}

		final Class< ? > s = c.getSuperclass();
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
	public static boolean isInstanceOf(final Class< ? > class1,
			final Class< ? > class2) {

		// same class?
		if (class1.equals(class2)) {
			return true;
		}

		// implements interface
		for (final Class< ? > clazz : class1.getInterfaces()) {
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
	public static void loadClassmap() {
		try {
			final ResourcePersistence resource = new ResourcePersistence(
					"org/encog/data/classes.txt");
			final InputStream is = resource.createInputStream();
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				final Class< ? > c = Class.forName(line);
				ReflectionUtil.classMap.put(c.getSimpleName(), c);
			}
			is.close();
		} catch (final IOException e) {
			throw new EncogError(e);
		} catch (final ClassNotFoundException e) {
			throw new EncogError(e);
		}

	}

	/**
	 * Resolve an encog class using its simple name.
	 * 
	 * @param name
	 *            The simple name of the class.
	 * @return The class requested.
	 */
	public static Class< ? > resolveEncogClass(final String name) {
        synchronized (ReflectionUtil.classMap) { 
            if (ReflectionUtil.classMap.size() == 0) {
                ReflectionUtil.loadClassmap();
            }
        }
        return ReflectionUtil.classMap.get(name);
	}

	/**
	 * Determine if Encog persistence should access the specified field.
	 * 
	 * @param field
	 *            The field to check.
	 * @param base
	 *            True if this is the actual Encog persisted class(top level)
	 * @return True if the class should be accessed.
	 */
	public static boolean shouldAccessField(final Field field,
			final boolean base) {
		if (field.getAnnotation(EGIgnore.class) != null) {
			return false;
		}

		if ((field.getModifiers() & Modifier.STATIC) == 0) {
			if (base) {
				if (field.getName().equalsIgnoreCase("name")
						|| field.getName().equalsIgnoreCase("description")) {
					return false;
				}
			}
			return true;
		}
		return false;
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
		final Class< ? > type = field.getType();
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
