/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
package org.encog.persist.persistors.generic;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.encog.EncogError;
import org.encog.parse.tags.Tag.Type;
import org.encog.parse.tags.read.ReadXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic class used to take an XML segment and produce an object for it.
 * Some of the Encog persistors make use of this class.
 * 
 * @author jheaton
 * 
 */
public class XML2Object {

	private ObjectMapper mapper = new ObjectMapper();
	
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Load an object from XML.
	 * 
	 * @param in
	 *            The XML reader.
	 * @param target
	 *            The object to load.
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public void load(final ReadXML in, final EncogPersistedObject target)  {
		this.mapper.clear();
		target.setName(in.getTag().getAttributeValue("name"));
		target.setDescription(in.getTag().getAttributeValue("description"));
		loadObject(null, in, target);
		this.mapper.resolve();
	}

	private void loadCollection(final ReadXML in, Collection<Object> collection) {
		try {
			while (in.readToTag()) {
				if (in.getTag().getType() == Type.BEGIN) {
					final String tagName = in.getTag().getName();
					Class<?> c = ReflectionUtil.resolveEncogClass(tagName);
					Object target = c.newInstance();
					loadObject(null, in, target);
					collection.add(target);
				} else if (in.getTag().getType() == Type.END) {
					return;
				}
			}
		} catch (InstantiationException e) {
			throw new EncogError(e);
		} catch (IllegalAccessException e) {
			throw new EncogError(e);
		}
	}
	
	private void setFieldValue(ReadXML in, Field field, Object target, String value)
	{
		try
		{
		
		final Class<?> type = field.getType();
		if (type == long.class) {
			field.setLong(target, Long.parseLong(value));
		} else if (type == int.class) {
			field.setInt(target, Integer.parseInt(value));
		} else if (type == short.class) {
			field.setShort(target, Short.parseShort(value));
		} else if (type == double.class) {
			field.setDouble(target, Double.parseDouble(value));
		} else if (type == float.class) {
			field.setDouble(target, Float.parseFloat(value));
		} else if (type == String.class) {
			field.set(target, value);
		}
		}
		catch(IllegalAccessException e)
		{
			throw new EncogError(e);
		}
		catch (final NumberFormatException e) {
			throw new EncogError(e);
		} 
	}
	
	private Object loadObject(final Field objectField, Object parent, final ReadXML in) throws InstantiationException, IllegalAccessException
	{
		String ref = in.getTag().getAttributeValue("ref");
		
		// handle ref
		if( ref!=null) {
			int ref2 = Integer.parseInt(in.getTag().getAttributeValue("ref"));
			this.mapper.addFieldMapping(ref2, objectField, parent);
			in.readToTag();
			return null;
		}
		else
		{	
			Class<?> c = ReflectionUtil.resolveEncogClass(in.getTag().getName());
			Object obj = c.newInstance();
			loadObject(objectField,in,obj);
			return obj;
		}
	}

	private void loadObject(final Field objectField, final ReadXML in, Object target) {

		try {
			// handle attributes
			for( String key: in.getTag().getAttributes().keySet() )
			{
				if( key.equals("native"))
					continue;
				
				// see if there is an id
				if( key.equals("id"))
				{
					int ref = Integer.parseInt(in.getTag().getAttributeValue("id"));
					this.mapper.addObjectMapping(ref, target);
					continue;
				}
				
				final Field field = ReflectionUtil.findField(target
						.getClass(), key);
				String value = in.getTag().getAttributeValue(key);
				setFieldValue(in,field,target,value);
			}
			
			// handle properties
			while (in.readToTag()) {
				if (in.getTag().getType() == Type.BEGIN) {
					final String tagName = in.getTag().getName();
					final Field field = ReflectionUtil.findField(target
							.getClass(), tagName);
					if (field == null)
						continue;
					field.setAccessible(true);
					Object currentValue = field.get(target);

					if( ReflectionUtil.isPrimitive(currentValue) ) {
						final String value = in.readTextToTag();
						setFieldValue(in,field,target,value);
					}
					else if (currentValue instanceof Collection) {
						loadCollection(in, (Collection<Object>) currentValue);
					} else if( field.getType() == File.class ) {
						final String value = in.readTextToTag();
						File file = new File(value);
						field.set(target, file);
					}
					else {
						in.readToTag();
						Object nextObject = loadObject(field,target,in);
						field.set(target, nextObject);
					}
				} else if (in.getTag().getType() == Type.END) {
					if (in.getTag().getName().equals(
							target.getClass().getSimpleName()))
						return;
				}
			}
		} catch (final IllegalArgumentException e) {
			throw new EncogError(e);
		} catch (final IllegalAccessException e) {
			throw new EncogError(e);
		} catch (InstantiationException e) {
			throw new EncogError(e);
		} 

	}

}
