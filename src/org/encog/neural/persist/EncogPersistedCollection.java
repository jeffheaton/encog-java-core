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
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.encog.neural.NeuralNetworkError;
import org.encog.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class EncogPersistedCollection {
	private List<EncogPersistedObject> list = new ArrayList<EncogPersistedObject>();

	private String platform;
	private int fileVersion;
	private String encogVersion;

	public void add(EncogPersistedObject obj) {
		this.list.add(obj);
	}

	public void save(String filename){
		try {
			OutputStream os = new FileOutputStream(filename);
			save(os);
			os.close();
		} catch (IOException e) {
			throw new NeuralNetworkError(e);
		}
	}

	public void save(OutputStream os) {
		try {
			StreamResult streamResult = new StreamResult(os);
			SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory
					.newInstance();
			// SAX2.0 ContentHandler.
			TransformerHandler hd = tf.newTransformerHandler();
			Transformer serializer = hd.getTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			// serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"enocg.dtd");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			hd.setResult(streamResult);
			hd.startDocument();
			AttributesImpl atts = new AttributesImpl();
			// USERS tag.
			hd.startElement("", "", "Document", atts);

			saveHeader(hd);
			saveObjects(hd);

			hd.endElement("", "", "Document");
			hd.endDocument();
		} catch (TransformerConfigurationException e) {
			throw new NeuralNetworkError(e);
		} catch (SAXException e) {
			throw new NeuralNetworkError(e);
		} 
	}

	private void saveHeader(TransformerHandler hd) throws SAXException {
		String data;
		AttributesImpl atts = new AttributesImpl();
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

	public static Persistor createPersistor(String name) {
		try {
			name += "Persistor";
			Class<?> c = Class.forName("org.encog.neural.persist.persistors."
					+ name);
			Persistor persistor = (Persistor) c.newInstance();
			return persistor;
		} catch (ClassNotFoundException e) {
			return null;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}

	private void saveObjects(TransformerHandler hd){
		AttributesImpl atts = new AttributesImpl();
		try {
			hd.startElement("", "", "objects", atts);
		

		for (EncogPersistedObject obj : this.list) {
			atts.clear();
			atts.addAttribute("", "", "native", "CDATA", obj.getClass()
					.getName());

			String name = obj.getClass().getSimpleName();
			Persistor persistor = EncogPersistedCollection.createPersistor(name);
			persistor.save(obj, hd);

		}

		hd.endElement("", "", "objects");
		}
		catch (SAXException e) {
			throw new NeuralNetworkError(e);
		}
	}

	public void load(String filename) {
		try {
			InputStream is = new FileInputStream(filename);
			load(is);
			is.close();
		} catch (IOException e) {
			throw new NeuralNetworkError(e);
		}
	}

	public void load(InputStream is) {
		try {
			// setup the XML parser stuff
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			DocumentBuilder db = null;
			db = dbf.newDocumentBuilder();

			Document doc = null;
			doc = db.parse(is);
			Element memory = doc.getDocumentElement();

			// read in the data

			// first count the number of training sets

			for (Node child = memory.getFirstChild(); child != null; child = child
					.getNextSibling()) {
				if (!(child instanceof Element))
					continue;
				Element node = (Element) child;

				if (node.getNodeName().equals("Header"))
					loadHeader(node);
				else if (node.getNodeName().equals("objects"))
					loadObjects(node);
			}

		} catch (javax.xml.parsers.ParserConfigurationException e) {
			throw new NeuralNetworkError(e);
		} catch (org.xml.sax.SAXException e) {
			throw new NeuralNetworkError(e);
		} catch (java.io.IOException e) {
			throw new NeuralNetworkError(e);
		}
	}

	private void loadObjects(Element objects)  {
		for (Node child = objects.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element))
				continue;
			Element node = (Element) child;

			Persistor persistor = EncogPersistedCollection.createPersistor(node.getNodeName());
			if (persistor != null) {
				EncogPersistedObject object = persistor.load(node);
				this.list.add(object);
			}
		}
	}

	private void loadHeader(Element node) {
		try {
			this.platform = XMLUtil.findElementAsString(node, "platform");
			this.encogVersion = XMLUtil.findElementAsString(node,
					"encogVersion");
			this.fileVersion = XMLUtil
					.findElementAsInt(node, "fileVersion", -1);
		} catch (Exception e) {
			throw new NeuralNetworkError(e);
		}

	}

	public void clear() {
		list.clear();
	}

	/**
	 * @return the list
	 */
	public List<EncogPersistedObject> getList() {
		return list;
	}

	/**
	 * @return the fileVersion
	 */
	public int getFileVersion() {
		return fileVersion;
	}

	/**
	 * @return the encogVersion
	 */
	public String getEncogVersion() {
		return encogVersion;
	}

	/**
	 * @return the platform
	 */
	public String getPlatform() {
		return platform;
	}

}
