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

import java.lang.reflect.Field;
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
 * Encog persistors make use of this class.
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

	private WriteXML out;
	private ObjectTagger tagger = new ObjectTagger();

	/**
	 * Save the object to XML.
	 * 
	 * @param obj
	 *            The object to save.
	 * @param out
	 *            The XML writer.
	 */
	public void save(final EncogPersistedObject encogObject, final WriteXML out) {
		this.out = out;
		try {
			PersistorUtil.beginEncogObject(encogObject.getClass()
					.getSimpleName(), out, encogObject, true);
			
			tagger.analyze(encogObject);

			for (final Field childField : ReflectionUtil.getAllFields(encogObject.getClass())) {
				if (ReflectionUtil.shouldAccessField(childField, true)) {
					childField.setAccessible(true);
					Object childValue = childField.get(encogObject);
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

	private void saveObject(Object parentObject)
			throws IllegalArgumentException, IllegalAccessException {
		// does this object have an ID?
		
		if( tagger.hasReference(parentObject) )
		{
			int id = tagger.getReference(parentObject);
			out.addAttribute("id", ""+id);
		}
		
		// get all fields
		Collection<Field> allFields = ReflectionUtil.getAllFields(parentObject.getClass());
		// handle attributes
		for (final Field childField : allFields) {
			childField.setAccessible(true);
			if (ReflectionUtil.shouldAccessField(childField, false) &&
					childField.getAnnotation(EGAttribute.class)!=null) {
				Object childValue = childField.get(parentObject);
				out.addAttribute(childField.getName(), childValue.toString());
			}
		}
		// handle actual fields		
		out.beginTag(parentObject.getClass().getSimpleName());
		for (final Field childField : allFields) {
			childField.setAccessible(true);			
			if (ReflectionUtil.shouldAccessField(childField, false)&&
					childField.getAnnotation(EGAttribute.class)==null) {
				
				Object childValue = childField.get(parentObject);
				
				out.beginTag(childField.getName());
				if( childField.getAnnotation(EGReference.class)!=null)
				{
					saveFieldReference(childValue);
				}
				else
				{					
					saveField(childValue);
					
				}	
				out.endTag();
			}
		}
		out.endTag();
	}
	
	private void saveFieldReference(Object fieldObject)
	{
		if( this.tagger.hasReference(fieldObject))
			out.addAttribute("ref", ""+this.tagger.getReference(fieldObject));
		else
			out.addAttribute("ref", "");
		
		out.beginTag(fieldObject.getClass().getSimpleName());
		out.endTag();
	}

	private void saveField(Object fieldObject) throws IllegalArgumentException,
			IllegalAccessException {
		if (fieldObject != null) {
			if (fieldObject instanceof Collection) {

				saveCollection(out, (Collection<?>) fieldObject);

			} else if (ReflectionUtil.isPrimitive(fieldObject) || ReflectionUtil.isSimple(fieldObject) ) {
				out.addText(fieldObject.toString());
			} else if (fieldObject instanceof String) {
				out.addText(fieldObject.toString());
			} else {
				saveObject(fieldObject);
			}
		}
	}
	
	/**
	 * Save a collection.
	 * 
	 * @param out
	 *            The XML writer.
	 * @param value
	 *            The value to save.
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private void saveCollection(final WriteXML out, final Collection<?> value)
			throws IllegalArgumentException, IllegalAccessException {

		for (final Object obj : value) {
			this.saveObject(obj);
		}
	}
}
