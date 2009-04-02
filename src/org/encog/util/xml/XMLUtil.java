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
package org.encog.util.xml;

import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Simple XML utilities for parsing.
 * @author jheaton
 */
public final class XMLUtil {
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Add the specified attribute.
	 * @param e The node to add the attribute to.
	 * @param name The name of the attribute.
	 * @param value The value of the attribute.
	 */
	public static void addAttribute(final Node e, final String name,
			final String value) {
		final Attr attr = e.getOwnerDocument().createAttribute(name);
		attr.setValue(value);
		e.getAttributes().setNamedItem(attr);
	}

	/**
	 * Create a property element.  Do not append it though!
	 * @param doc The document to use.
	 * @param name The name of the property.
	 * @param value The value to add to the property.
	 * @return The newly created property.
	 */
	public static Node createProperty(final Document doc, final String name,
			final String value) {
		final Node n = doc.createElement(name);
		n.appendChild(doc.createTextNode(value));
		return n;
	}

	/**
	 * Find a child element.
	 * @param e The element to search.
	 * @param find The name to search for.
	 * @return The element found, or null if not found.
	 */
	public static Element findElement(final Element e, final String find) {
		for (Node child = e.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element)) {
				continue;
			}
			final Element el = (Element) child;
			if (el.getNodeName().equals(find)) {
				return el;
			}
		}
		return null;
	}

	/**
	 * Find an element, return as an int.
	 * @param e The element that searches.
	 * @param find What we are searching for.
	 * @param def The default value, if we fail to find it.
	 * @return The value found, default value otherwise.
	 */
	public static int findElementAsInt(final Element e, final String find,
			final int def) {
		final String str = findElementAsString(e, find);
		if (str == null) {
			return def;
		}
		try {
			return Integer.parseInt(str);
		} catch (final NumberFormatException ex) {
			return def;
		}
	}

	/**
	 * Find an element, return as a long.
	 * @param e The element that searches.
	 * @param find What we are searching for.
	 * @param def The default value, if we fail to find it.
	 * @return The value found, default value otherwise.
	 */
	public static long findElementAsLong(final Element e, final String find,
			final long def) {
		final String str = findElementAsString(e, find);
		if (str == null) {
			return def;
		}
		try {
			return Long.parseLong(str);
		} catch (final NumberFormatException ex) {
			return def;
		}
	}

	/**
	 * Find an element, return as a string.
	 * @param e The element that searches.
	 * @param find What we are searching for.
	 * @return The value found, default value otherwise.
	 */
	public static String findElementAsString(final Element e, 
			final String find) {
		final Element el = findElement(e, find);

		if (el == null) {
			return null;
		}

		for (Node child = el.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Text)) {
				continue;
			}
			return child.getNodeValue();
		}
		return null;
	}

	/**
	 * Get the specified element's text value.
	 * @param e The element.
	 * @return The text value of the specified element.
	 */
	public static String getElementValue(final Element e) {
		for (Node child = e.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Text)) {
				continue;
			}
			return child.getNodeValue();
		}
		return null;
	}

	/**
	 * Private constructor.
	 */
	private XMLUtil() {
	}

	public static TransformerHandler saveXML(OutputStream os) throws TransformerConfigurationException
	{
		final StreamResult streamResult = new StreamResult(os);
		final SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory
				.newInstance();
		// SAX2.0 ContentHandler.
		final TransformerHandler hd = tf.newTransformerHandler();
		final Transformer serializer = hd.getTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		hd.setResult(streamResult);
		return hd;
	}
	
}
