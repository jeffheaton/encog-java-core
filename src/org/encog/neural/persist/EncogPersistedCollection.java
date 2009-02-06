/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
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

package org.encog.neural.persist;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.encog.EncogError;
import org.encog.neural.NeuralNetworkError;
import org.encog.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * An EncogPersistedCollection holds a collection of EncogPersistedObjects. This
 * allows the various neural networks and some data sets to be peristed. They
 * are persisted to an XML form.
 * 
 * @author jheaton
 * 
 */
public class EncogPersistedCollection {

	/**
	 * Create a persistor object. These objects know how to persist certain
	 * types of classes.
	 * 
	 * @param className
	 *            The name of the class to create a persistor for.
	 * @return The persistor for the specified class.
	 */
	public static Persistor createPersistor(final String className) {
		try {
			String name = className + "Persistor";
			final Class<?> c = Class
					.forName("org.encog.neural.persist.persistors." + name);
			final Persistor persistor = (Persistor) c.newInstance();
			return persistor;
		} catch (final ClassNotFoundException e) {
			return null;
		} catch (final InstantiationException e) {
			return null;
		} catch (final IllegalAccessException e) {
			return null;
		}
	}

	/**
	 * The object to be persisted.
	 */
	private final List<EncogPersistedObject> list = new ArrayList<EncogPersistedObject>();

	/**
	 * The platform this collection was created on.
	 */
	private String platform;

	/**
	 * The version of the persisted file.
	 */
	private int fileVersion;

	/**
	 * The version of Encog.
	 */
	private String encogVersion;

	/**
	 * Add an EncogPersistedObject to the collection.
	 * 
	 * @param obj
	 *            The object to add.
	 */
	public void add(final EncogPersistedObject obj) {
		this.list.add(obj);
	}

	/**
	 * Clear the collection.
	 */
	public void clear() {
		this.list.clear();
	}

	/**
	 * @return the encogVersion
	 */
	public String getEncogVersion() {
		return this.encogVersion;
	}

	/**
	 * @return the fileVersion
	 */
	public int getFileVersion() {
		return this.fileVersion;
	}

	/**
	 * @return the list
	 */
	public List<EncogPersistedObject> getList() {
		return this.list;
	}

	/**
	 * @return the platform
	 */
	public String getPlatform() {
		return this.platform;
	}

	/**
	 * Load from an input stream.
	 * 
	 * @param is
	 *            The stream to load from.
	 */
	public void load(final InputStream is) {
		try {
			// setup the XML parser stuff
			final DocumentBuilderFactory dbf = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder db = null;
			db = dbf.newDocumentBuilder();

			Document doc = null;
			doc = db.parse(is);
			final Element memory = doc.getDocumentElement();

			// read in the data

			// first count the number of training sets

			for (Node child = memory.getFirstChild(); child != null; child = child
					.getNextSibling()) {
				if (!(child instanceof Element)) {
					continue;
				}
				final Element node = (Element) child;

				if (node.getNodeName().equals("Header")) {
					loadHeader(node);
				} else if (node.getNodeName().equals("objects")) {
					loadObjects(node);
				}
			}

		} catch (final javax.xml.parsers.ParserConfigurationException e) {
			throw new NeuralNetworkError(e);
		} catch (final org.xml.sax.SAXException e) {
			throw new NeuralNetworkError(e);
		} catch (final java.io.IOException e) {
			throw new NeuralNetworkError(e);
		}
	}

	/**
	 * Load from a file.
	 * 
	 * @param filename
	 *            The filename to load from.
	 */
	public void load(final String filename) {
		try {
			final InputStream is = new FileInputStream(filename);
			load(is);
			is.close();
		} catch (final IOException e) {
			throw new NeuralNetworkError(e);
		}
	}

	/**
	 * Load the XML header.
	 * 
	 * @param node
	 *            The node to load from.
	 */
	private void loadHeader(final Element node) {
		try {
			this.platform = XMLUtil.findElementAsString(node, "platform");
			this.encogVersion = XMLUtil.findElementAsString(node,
					"encogVersion");
			this.fileVersion = XMLUtil
					.findElementAsInt(node, "fileVersion", -1);
		} catch (final Exception e) {
			throw new NeuralNetworkError(e);
		}

	}

	/**
	 * Load the objects list.
	 * 
	 * @param objects
	 *            The node to load from.
	 */
	private void loadObjects(final Element objects) {
		for (Node child = objects.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element)) {
				continue;
			}
			final Element node = (Element) child;

			final Persistor persistor = EncogPersistedCollection
					.createPersistor(node.getNodeName());
			if (persistor != null) {
				final EncogPersistedObject object = persistor.load(node);
				this.list.add(object);
			}
		}
	}

	/**
	 * Save to an output stream.
	 * 
	 * @param os
	 *            The stream to save to.
	 */
	public void save(final OutputStream os) {
		try {
			final TransformerHandler hd = XMLUtil.saveXML(os);
				
			hd.startDocument();
			final AttributesImpl atts = new AttributesImpl();
			// USERS tag.
			hd.startElement("", "", "Document", atts);

			saveHeader(hd);
			saveObjects(hd);

			hd.endElement("", "", "Document");
			hd.endDocument();
		} catch (final TransformerConfigurationException e) {
			throw new NeuralNetworkError(e);
		} catch (final SAXException e) {
			throw new NeuralNetworkError(e);
		}
	}

	/**
	 * Save to a file.
	 * 
	 * @param filename
	 *            The filename to save to.
	 */
	public void save(final String filename) {
		try {
			final OutputStream os = new FileOutputStream(filename);
			save(os);
			os.close();
		} catch (final IOException e) {
			throw new NeuralNetworkError(e);
		}
	}

	/**
	 * Save the XML header.
	 * 
	 * @param hd
	 *            The object to save to.
	 * @throws SAXException
	 *             An error processing the XML.
	 */
	private void saveHeader(final TransformerHandler hd) throws SAXException {
		String data;
		final AttributesImpl atts = new AttributesImpl();
		hd.startElement("", "", "Header", atts);

		// platform
		hd.startElement("", "", "platform", atts);
		data = "Java";
		hd.characters(data.toCharArray(), 0, data.length());
		hd.endElement("", "", "platform");

		// platform
		hd.startElement("", "", "fileVersion", atts);
		data = "0";
		hd.characters(data.toCharArray(), 0, data.length());
		hd.endElement("", "", "fileVersion");

		// platform
		hd.startElement("", "", "encogVersion", atts);
		data = "1.0.0";
		hd.characters(data.toCharArray(), 0, data.length());
		hd.endElement("", "", "encogVersion");

		hd.endElement("", "", "Header");
	}

	/**
	 * Save the list of objects.
	 * 
	 * @param hd
	 *            The persistance object.
	 */
	private void saveObjects(final TransformerHandler hd) {
		final AttributesImpl atts = new AttributesImpl();
		try {
			hd.startElement("", "", "objects", atts);

			for (final EncogPersistedObject obj : this.list) {
				atts.clear();
				atts.addAttribute("", "", "native", "CDATA", obj.getClass()
						.getName());

				final String name = obj.getClass().getSimpleName();
				Persistor persistor = EncogPersistedCollection
						.createPersistor(name);

				if (persistor == null) {
					persistor = obj.createPersistor();
				}

				if (persistor == null) {
					throw new EncogError("No persistor defined for object: "
							+ name);
				}

				persistor.save(obj, hd);

			}

			hd.endElement("", "", "objects");
		} catch (final SAXException e) {
			throw new NeuralNetworkError(e);
		}
	}

	public static void addAttribute(final AttributesImpl atts,
			final String name, final String value) {
		String v = value;
		if (v == null)
			v = "";
		atts.addAttribute("", "", name, "CDATA", v);
	}

	public static AttributesImpl createAttributes(final EncogPersistedObject obj) {
		AttributesImpl result = new AttributesImpl();
		EncogPersistedCollection.addAttribute(result, "native", ""
				+ obj.getClass().getName());
		EncogPersistedCollection.addAttribute(result, "name", obj.getName());
		EncogPersistedCollection.addAttribute(result, "description", obj
				.getDescription());
		return result;
	}

	public static void addProperty(final TransformerHandler hd, String name,
			String value) {
		try {
			AttributesImpl atts = new AttributesImpl();
			hd.startElement("", "", name, atts);
			hd.characters(value.toCharArray(), 0, value.length());
			hd.endElement("", "", name);
		} catch (SAXException e) {
			throw new EncogError(e);
		}
	}

	/**
	 * Called to search all Encog objects in this collection for one with a name
	 * that passes what was passed in.
	 * 
	 * @param name
	 *            The name we are searching for.
	 * @return The Encog object with the correct name.
	 */
	public EncogPersistedObject find(String name) {
		for (EncogPersistedObject obj : this.list) {
			if (name.equals(obj.getName())) {
				return obj;
			}
		}
		return null;
	}

	public void delete(String name) {
		Object[] array = this.list.toArray();

		for (int i = 0; i < array.length; i++) {
			EncogPersistedObject obj = (EncogPersistedObject) array[i];
			if (name.equals(obj.getName())) {
				this.list.remove(obj);
			}
		}

	}

	public void loadResource(String resourceName) {
		try {
			ClassLoader loader = this.getClass().getClassLoader();
			InputStream is = loader.getResourceAsStream(resourceName);
			if (is == null)
				throw new EncogError("Can't read resource: " + resourceName);
			load(is);
			is.close();
		} catch (IOException e) {
			throw new EncogError(e);
		}
	}

}
