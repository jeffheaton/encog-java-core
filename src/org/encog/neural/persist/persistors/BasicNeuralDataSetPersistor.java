/*
 * Encog Artificial Intelligence Framework v1.x
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
package org.encog.neural.persist.persistors;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.persist.EncogPersistedCollection;
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Persist the basic neural data set.
 * @author jheaton
 *
 */
public class BasicNeuralDataSetPersistor implements Persistor {

	/**
	 * The name of the pair XML tag.
	 */
	private String pairXML;
	
	/**
	 * The name of the input XML tag.
	 */
	private String inputXML;
	
	/**
	 * The name of the ideal XML tag.
	 */
	private String idealXML;
	
	/**
	 * The name of the value XML tag.
	 */
	private String valueXML;

	/**
	 * Construct an object with default values.
	 */
	public BasicNeuralDataSetPersistor() {
		this.pairXML = "NeuralDataPair";
		this.inputXML = "input";
		this.idealXML = "ideal";
		this.valueXML = "value";
	}

	/**
	 * @return the idealXML
	 */
	public String getIdealXML() {
		return this.idealXML;
	}

	/**
	 * @return the inputXML
	 */
	public String getInputXML() {
		return this.inputXML;
	}

	/**
	 * @return the pairXML
	 */
	public String getPairXML() {
		return this.pairXML;
	}

	/**
	 * @return the valueXML
	 */
	public String getValueXML() {
		return this.valueXML;
	}

	/**
	 * Load from the specified node.
	 * 
	 * @param pairs
	 *            The pairs to load.
	 * @return The EncogPersistedObject that was loaded.
	 */
	public EncogPersistedObject load(final Element pairs) {

		final String name = pairs.getAttribute("name");
		final String description = pairs.getAttribute("description");
		final BasicNeuralDataSet result = new BasicNeuralDataSet();
		result.setName(name);
		result.setDescription(description);
		
		for (Node child = pairs.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element)) {
				continue;
			}
			final Element node = (Element) child;
			if (child.getNodeName().equals(this.pairXML)) {
				final NeuralDataPair pair = loadPair(node);
				result.add(pair);
			}
		}
		return result;
	}

	/**
	 * Load a NeuralDataPair.
	 * @param node The node to load from.
	 * @return The NeuralDataPair object that was loaded.
	 */
	private NeuralDataPair loadPair(final Element node) {
		final Element inputNode = XMLUtil.findElement(node, this.inputXML);
		final Element idealNode = XMLUtil.findElement(node, this.idealXML);
		final NeuralData input = loadValues(inputNode);
		final NeuralData ideal = loadValues(idealNode);
		return new BasicNeuralDataPair(input, ideal);
	}

	/**
	 * Load the values.
	 * @param inputNode The node to read from.
	 * @return The neural data that was loaded.
	 */
	private NeuralData loadValues(final Element inputNode) {
		final List<Double> list = new ArrayList<Double>();

		for (Node child = inputNode.getFirstChild(); 
		child != null; child = child
				.getNextSibling()) {
			if (!(child instanceof Element)) {
				continue;
			}

			if (child.getNodeName().equals(this.valueXML)) {
				final String str = child.getTextContent();
				final double d = Double.parseDouble(str);
				list.add(d);
			}
		}

		int i = 0;
		final NeuralData result = new BasicNeuralData(list.size());
		for (final double d : list) {
			result.setData(i++, d);
		}

		return result;

	}

	/**
	 * Save the specified object.
	 * 
	 * @param object
	 *            The object to save.
	 * @param hd
	 *            The XML object.
	 */
	public void save(final EncogPersistedObject object,
			final TransformerHandler hd) {
		try {
			final BasicNeuralDataSet set = (BasicNeuralDataSet) object;

			final AttributesImpl atts = 
				EncogPersistedCollection.createAttributes(object);
			hd.startElement("", "", "BasicNeuralDataSet", atts);

			atts.clear();
			for (final NeuralDataPair pair : set) {
				hd.startElement("", "", this.pairXML, atts);
				hd.startElement("", "", this.inputXML, atts);
				for (int i = 0; i < pair.getInput().size(); i++) {
					hd.startElement("", "", this.valueXML, atts);
					final String data = "" + pair.getInput().getData(i);
					hd.characters(data.toCharArray(), 0, data.length());
					hd.endElement("", "", this.valueXML);
				}
				hd.endElement("", "", this.inputXML);
				hd.startElement("", "", this.idealXML, atts);
				for (int i = 0; i < pair.getIdeal().size(); i++) {
					hd.startElement("", "", this.valueXML, atts);
					final String data = "" + pair.getIdeal().getData(i);
					hd.characters(data.toCharArray(), 0, data.length());
					hd.endElement("", "", this.valueXML);
				}
				hd.endElement("", "", this.idealXML);
				hd.endElement("", "", this.pairXML);
			}

			hd.endElement("", "", "BasicNeuralDataSet");
		} catch (final SAXException e) {
			throw new NeuralNetworkError(e);
		}

	}

	/**
	 * @param idealXML
	 *            the idealXML to set
	 */
	public void setIdealXML(final String idealXML) {
		this.idealXML = idealXML;
	}

	/**
	 * @param inputXML
	 *            the inputXML to set
	 */
	public void setInputXML(final String inputXML) {
		this.inputXML = inputXML;
	}

	/**
	 * @param pairXML
	 *            the pairXML to set
	 */
	public void setPairXML(final String pairXML) {
		this.pairXML = pairXML;
	}

	/**
	 * @param valueXML
	 *            the valueXML to set
	 */
	public void setValueXML(final String valueXML) {
		this.valueXML = valueXML;
	}

}
