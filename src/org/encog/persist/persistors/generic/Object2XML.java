/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.persist.persistors.generic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.PersistError;
import org.encog.persist.annotations.EGAttribute;
import org.encog.persist.annotations.EGReference;
import org.encog.persist.persistors.PersistorUtil;
import org.encog.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic class used to take an object and produce XML for it. Some of the
 * Encog persistors make use of this class. The Encog generic persistor makes
 * use of this class.
 * 
 * @author jheaton
 * 
 */
public class Object2XML {

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String REFF_ID = "idx";
	
	
	/**
	 * The XML writer used.
	 */
	private WriteXML out;
	
	/**
	 * The object tagger, allows the objects to be tagged with references.
	 */
	private final ObjectTagger tagger = new ObjectTagger();

	/**
	 * Save the object to XML.
	 * 
	 * @param encogObject
	 *            The object to save.
	 * @param out
	 *            The XML writer.
	 */
	public void save(final EncogPersistedObject encogObject, 
			final WriteXML out) {
		this.out = out;
		try {
			PersistorUtil.beginEncogObject(encogObject.getClass()
					.getSimpleName(), out, encogObject, true);

			this.tagger.analyze(encogObject);

			for (final Field childField : ReflectionUtil
					.getAllFields(encogObject.getClass())) {
				if (ReflectionUtil.shouldAccessField(childField, true)) {
					childField.setAccessible(true);
					final Object childValue = childField.get(encogObject);
					out.beginTag(childField.getName());
					saveField(childValue);
					out.endTag();
				}
			}

			out.endTag();
		} catch (final IllegalAccessException e) {
			throw new PersistError(e);
		}

	}

	/**
	 * Save a collection.
	 * 
	 * @param value
	 *            The collection to save
	 * @throws IllegalAccessException An error.
	 */
	private void saveCollection(final Collection< ? > value)
			throws IllegalAccessException {

		for (final Object obj : value) {
			saveObject(obj);
		}
	}

	/**
	 * Save a field.
	 * @param fieldObject The field to save.
	 * @throws IllegalAccessException An error.
	 */
	private void saveField(final Object fieldObject)
			throws IllegalAccessException {
		if (fieldObject != null) {
			if (fieldObject instanceof Collection< ? >) {

				saveCollection((Collection< ? >) fieldObject);

			} else if( fieldObject.getClass().isEnum() ) {
				this.out.addText(fieldObject.toString());
			} else if (ReflectionUtil.isPrimitive(fieldObject)
					|| ReflectionUtil.isSimple(fieldObject)) {
				this.out.addText(fieldObject.toString());
			} else if (fieldObject instanceof String) {
				this.out.addText(fieldObject.toString());
			} else {
				saveObject(fieldObject);
			}
		}
	}

	/**
	 * Save a field by reference.
	 * @param fieldObject The field to save.
	 */
	private void saveFieldReference(final Object fieldObject) {
		if (this.tagger.hasReference(fieldObject)) {
			this.out.addAttribute("ref", ""
					+ this.tagger.getReference(fieldObject));
		} else {
			this.out.addAttribute("ref", "");
		}

		this.out.beginTag(fieldObject.getClass().getSimpleName());
		this.out.endTag();
	}

	/**
	 * Save an object.
	 * @param obj The object.
	 * @throws IllegalAccessException An error.
	 */
	private void saveObject(final Object obj)
			throws IllegalAccessException {
		// does this object have an ID?		
		if (this.tagger.hasReference(obj)) {
			final int id = this.tagger.getReference(obj);
			this.out.addAttribute(REFF_ID, "" + id);
		}

		// get all fields
		final Collection<Field> allFields = ReflectionUtil
				.getAllFields(obj.getClass());
		// handle attributes
		for (final Field childField : allFields) {
			childField.setAccessible(true);
			if (ReflectionUtil.shouldAccessField(childField, false)
					&& (childField.getAnnotation(EGAttribute.class) != null)) {
				final Object childValue = childField.get(obj);
				this.out.addAttribute(childField.getName(), childValue
						.toString());
			}
		}
		// handle actual fields
		this.out.beginTag(obj.getClass().getSimpleName());
		for (final Field childField : allFields) {
			childField.setAccessible(true);
			if (ReflectionUtil.shouldAccessField(childField, false)
					&& (childField.getAnnotation(EGAttribute.class) == null)) {

				final Object childValue = childField.get(obj);

				this.out.beginTag(childField.getName());
				if (childField.getAnnotation(EGReference.class) != null) {
					saveFieldReference(childValue);
				} else {
					saveField(childValue);

				}
				this.out.endTag();
			}
		}
		this.out.endTag();
	}
}
