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
package org.encog.persist.persistors;

import org.encog.parse.ParseError;
import org.encog.parse.ParseTemplate;
import org.encog.parse.recognize.Recognize;
import org.encog.parse.recognize.RecognizeElement;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

/**
 * The Encog persistor used to persist the ParseTemplate class.
 * 
 * @author jheaton
 */
public class ParseTemplatePersistor implements Persistor {

	/**
	 * The template currently being processed.
	 */
	private ParseTemplate template;

	/**
	 * Load ParseTemplate object.
	 * 
	 * @param in
	 *            The XML to read it from.
	 * @return The loaded object.
	 */
	public EncogPersistedObject load(final ReadXML in) {
		this.template = new ParseTemplate();

		final String name = in.getTag().getAttributeValue("name");
		final String description = in.getTag().getAttributeValue("description");

		this.template.setName(name);
		this.template.setDescription(description);

		final String end = in.getTag().getName();
		while (in.readToTag()) {
			if (in.is("recognize", true)) {
				loadRecognize(in);
			} else if (in.is(end, false)) {
				break;
			}

		}

		return this.template;
	}

	/**
	 * Load the specified char processor.
	 * @param element The XML recognize element.
	 * @param in The XML reader.
	 */
	private void loadChar(final RecognizeElement element, final ReadXML in) {
		final String value = in.getTag().getAttributeValue("value");
		final String from = in.getTag().getAttributeValue("from");
		final String to = in.getTag().getAttributeValue("to");
		if (value != null) {
			element.add(value.charAt(0));
		} else {
			element.addRange(from.charAt(0), to.charAt(0));
		}
	}

	/**
	 * Load the specified recognize element.
	 * @param recognize The element to load.
	 * @param in The XML reader.
	 */
	private void loadElement(final Recognize recognize, final ReadXML in) {
		final String type = in.getTag().getAttributeValue("type");
		RecognizeElement recognizeElement;

		if (type.equals("ALLOW_ONE")) {
			recognizeElement = recognize
					.createElement(RecognizeElement.ALLOW_ONE);
		} else if (type.equals("ALLOW_MULTIPLE")) {
			recognizeElement = recognize
					.createElement(RecognizeElement.ALLOW_MULTIPLE);
		} else {
			recognizeElement = null; // ERROR
		}

		final String end = in.getTag().getName();
		while (in.readToTag()) {
			if (in.is("char", true)) {
				loadChar(recognizeElement, in);
			} else if (in.is("unit", true)) {
				loadUnit(recognizeElement, in);
			} else if (in.is(end, false)) {
				break;
			}

		}

	}

	/**
	 * Load the current recognize object.
	 * @param in The XML reader.
	 */
	private void loadRecognize(final ReadXML in) {
		final String id = in.getTag().getAttributeValue("id");
		final String ignore = in.getTag().getAttributeValue("ignore");
		final String recognizeClass = in.getTag().getAttributeValue("class");

		final Recognize recognize = new Recognize(id);
		try {
			if (recognizeClass != null) {
				recognize.setSignalClass(Class.forName(recognizeClass));
			}
		} catch (final ClassNotFoundException e) {
			throw new ParseError(e);
		}

		if ("true".equalsIgnoreCase(ignore)) {
			recognize.setIgnore(true);
		} else {
			recognize.setIgnore(false);
		}

		final String end = in.getTag().getName();
		while (in.readToTag()) {
			if (in.is("element", true)) {
				loadElement(recognize, in);
			} else if (in.is(end, false)) {
				break;
			}

		}

		this.template.addRecognizer(recognize);
	}

	/**
	 * Load the specified unit.
	 * @param element The recognize element
	 * @param in The specified XML reader. 
	 */
	private void loadUnit(final RecognizeElement element, final ReadXML in) {
		final String type = in.getTag().getAttributeValue("type");
		final String value = in.getTag().getAttributeValue("value");
		element.setType(type);
		if ((value != null) && !value.equals("")) {
			element.addAcceptedSignal(type, value);
		} else {
			element.addAcceptedSignal(type, null);
		}
	}

	/**
	 * Save the parse template.
	 * @param object The object to save.
	 * @param out The XML writer.
	 */
	public void save(final EncogPersistedObject object, final WriteXML out) {
		// TODO Auto-generated method stub

	}

}
