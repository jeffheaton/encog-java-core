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

import org.encog.neural.NeuralNetworkException;
import org.encog.neural.persist.persistors.BasicNetworkPersistor;
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

	public void save(String filename) throws NeuralNetworkException {
		try {
			OutputStream os = new FileOutputStream(filename);
			save(os);
			os.close();
		} catch (IOException e) {
			throw new NeuralNetworkException(e);
		}
	}

	public void save(OutputStream os) throws NeuralNetworkException {
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
			throw new NeuralNetworkException(e);
		} catch (SAXException e) {
			throw new NeuralNetworkException(e);
		} catch (ClassNotFoundException e) {
			throw new NeuralNetworkException(e);
		} catch (InstantiationException e) {
			throw new NeuralNetworkException(e);
		} catch (IllegalAccessException e) {
			throw new NeuralNetworkException(e);
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
			Class c = Class.forName("org.encog.neural.persist.persistors."
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

	private void saveObjects(TransformerHandler hd) throws SAXException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, NeuralNetworkException {
		AttributesImpl atts = new AttributesImpl();
		hd.startElement("", "", "objects", atts);

		for (EncogPersistedObject obj : this.list) {
			atts.clear();
			atts.addAttribute("", "", "native", "CDATA", obj.getClass()
					.getName());

			String name = obj.getClass().getSimpleName();
			Persistor persistor = this.createPersistor(name);
			persistor.save(obj, hd);

		}

		hd.endElement("", "", "objects");
	}

	public void load(String filename) throws NeuralNetworkException {
		try {
			InputStream is = new FileInputStream(filename);
			load(is);
			is.close();
		} catch (IOException e) {
			throw new NeuralNetworkException(e);
		}
	}

	public void load(InputStream is) throws NeuralNetworkException {
		try {
			System.out.println("Loading");
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
			throw new NeuralNetworkException(e);
		} catch (org.xml.sax.SAXException e) {
			throw new NeuralNetworkException(e);
		} catch (java.io.IOException e) {
			throw new NeuralNetworkException(e);
		}
	}

	private void loadObjects(Element objects) throws NeuralNetworkException  {
		for (Node child = objects.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element))
				continue;
			Element node = (Element) child;

			Persistor persistor = this.createPersistor(node.getNodeName());
			if (persistor != null) {
				EncogPersistedObject object = persistor.load(node);
				this.list.add(object);
			}
		}
	}

	private void loadHeader(Element node) throws NeuralNetworkException {
		try {
			this.platform = XMLUtil.findElementAsString(node, "platform");
			this.encogVersion = XMLUtil.findElementAsString(node,
					"encogVersion");
			this.fileVersion = XMLUtil
					.findElementAsInt(node, "fileVersion", -1);
		} catch (Exception e) {
			throw new NeuralNetworkException(e);
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
