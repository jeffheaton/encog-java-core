/*
 * Encog(tm) Core v2.4
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

package org.encog.persist.persistors.generic;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;

import org.encog.EncogError;
import org.encog.parse.tags.Tag.Type;
import org.encog.parse.tags.read.ReadXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic class used to take an XML segment and produce an object for it.
 * Some of the Encog persistors make use of this class. The Encog generic
 * persistor makes use of this class.
 * 
 * @author jheaton
 * 
 */
public class XML2Object {

	/**
	 * The object mapper to use to resolve references.
	 */
	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Used to read the XML.
	 */
	private ReadXML in;

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
	public void load(final ReadXML in, final EncogPersistedObject target) {
		this.in = in;
		this.mapper.clear();
		target.setName(in.getTag().getAttributeValue("name"));
		target.setDescription(in.getTag().getAttributeValue("description"));
		loadActualObject(null, target);
		this.mapper.resolve();
	}

	/**
	 * Load an object.
	 * 
	 * @param objectField
	 *            The object's field.
	 * @param target The object that will get the value.
	 */
	@SuppressWarnings("unchecked")
	private void loadActualObject(final Field objectField, final Object target) {

		try {
			// handle attributes
			for (final String key : this.in.getTag().getAttributes().keySet()) {
				if (key.equals("native")) {
					continue;
				}

				// see if there is an id
				if (key.equals("id")) {
					final int ref = Integer.parseInt(this.in.getTag()
							.getAttributeValue("id"));
					this.mapper.addObjectMapping(ref, target);
					continue;
				}

				final Field field = ReflectionUtil.findField(target.getClass(),
						key);
				if( field!=null ) {
					final String value = this.in.getTag().getAttributeValue(key);
					setFieldValue(field, target, value);
				}
			}

			// handle properties
			while (this.in.readToTag()) {
				if (this.in.getTag().getType() == Type.BEGIN) {
					final String tagName = this.in.getTag().getName();
					final Field field = ReflectionUtil.findField(target
							.getClass(), tagName);
					if (field == null) {
						continue;
					}
					field.setAccessible(true);
					final Object currentValue = field.get(target);

					if (ReflectionUtil.isPrimitive(currentValue)) {
						final String value = this.in.readTextToTag();
						setFieldValue(field, target, value);
					} else if (currentValue instanceof Collection) {
						loadCollection((Collection<Object>) currentValue);
					} else if (field.getType() == File.class) {
						final String value = this.in.readTextToTag();
						final File file = new File(value);
						field.set(target, file);
					} else {
						this.in.readToTag();
						final Object nextObject = loadObject(field, target);
						field.set(target, nextObject);
					}
				} else if (this.in.getTag().getType() == Type.END) {
					if (this.in.getTag().getName().equals(
							target.getClass().getSimpleName())) {
						return;
					}
				}
			}
		} catch (final IllegalArgumentException e) {
			throw new EncogError(e);
		} catch (final IllegalAccessException e) {
			throw new EncogError(e);
		} catch (final InstantiationException e) {
			throw new EncogError(e);
		}

	}

	/**
	 * Load a collection.
	 * @param collection The collection to load.
	 */
	private void loadCollection(final Collection<Object> collection) {
		try {
			while (this.in.readToTag()) {
				if (this.in.getTag().getType() == Type.BEGIN) {
					final String tagName = this.in.getTag().getName();
					final Class< ? > c = ReflectionUtil
							.resolveEncogClass(tagName);
					final Object target = c.newInstance();
					loadActualObject(null, target);
					collection.add(target);
				} else if (this.in.getTag().getType() == Type.END) {
					return;
				}
			}
		} catch (final InstantiationException e) {
			throw new EncogError(e);
		} catch (final IllegalAccessException e) {
			throw new EncogError(e);
		}
	}

	/**
	 * Load an object and handle reference if needed.
	 * @param objectField The field.
	 * @param parent The object that holds the field.
	 * @return The loaded object.
	 * @throws InstantiationException An error.
	 * @throws IllegalAccessException An error.
	 */
	private Object loadObject(final Field objectField, final Object parent)
			throws InstantiationException, IllegalAccessException {
		final String ref = this.in.getTag().getAttributeValue("ref");

		// handle ref
		if (ref != null) {
			final int ref2 = Integer.parseInt(this.in.getTag()
					.getAttributeValue("ref"));
			this.mapper.addFieldMapping(ref2, objectField, parent);
			this.in.readToTag();
			return null;
		} else {
			final Class< ? > c = ReflectionUtil.resolveEncogClass(this.in
					.getTag().getName());
			if (c == null) {
				throw new PersistError("Can't create class: "
						+ this.in.getTag().getName());
			}
			final Object obj = c.newInstance();
			loadActualObject(objectField, obj);
			return obj;
		}
	}

	/**
	 * Set a field value.
	 * @param field The field to set.
	 * @param target The object that contains the field.
	 * @param value The field value.
	 */
	private void setFieldValue(final Field field, final Object target,
			final String value) {
		try {

			final Class< ? > type = field.getType();
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
			} else if (type == boolean.class) {
				field.setBoolean(target,
						value.equalsIgnoreCase("true") ? Boolean.TRUE
								: Boolean.FALSE);
			}
		} catch (final IllegalAccessException e) {
			throw new EncogError(e);
		} catch (final NumberFormatException e) {
			throw new EncogError(e);
		}
	}

}
