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

package org.encog.neural.data.xml;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A data source that reads XML files.  This class is memory based, 
 * so large enough datasets could cause memory issues.
 */
public class XMLNeuralDataSet extends BasicNeuralDataSet {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -5960796361565902008L;
	/**
	 * Error Message: adds are not supported.
	 */
	public static final String ADD_NOT_SUPPORTED = 
		"Adds are not supported with this dataset, it is read only.";

	/**
	 * Construct an XML neural data set.
	 * @param filename The filename to load.
	 * @param pairXML The tag name for pairs.
	 * @param inputXML The tag name for input.
	 * @param idealXML The tag name for ideal.
	 * @param valueXML The tag name for actual values.
	 */
	public XMLNeuralDataSet(
			final String filename, 
			final String pairXML, 
			final String inputXML,
			final String idealXML, 
			final String valueXML) {

		try {
			InputStream is = new FileInputStream(filename);

			// setup the XML parser stuff
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			DocumentBuilder db = null;
			db = dbf.newDocumentBuilder();

			Document doc = null;
			doc = db.parse(is);
			Element node = doc.getDocumentElement();

			// read in the data

			/*BasicNeuralDataSetPersistor persistor = 
				new BasicNeuralDataSetPersistor();

			persistor.setIdealXML(idealXML);
			persistor.setInputXML(inputXML);
			persistor.setPairXML(pairXML);
			persistor.setValueXML(valueXML);*/

			//BasicNeuralDataSet set = (BasicNeuralDataSet) persistor.load(node);
			//this.setData(set.getData());

			is.close();

		} catch (javax.xml.parsers.ParserConfigurationException e) {
			throw new NeuralNetworkError(e);
		} catch (org.xml.sax.SAXException e) {
			throw new NeuralNetworkError(e);
		} catch (java.io.IOException e) {
			throw new NeuralNetworkError(e);
		}
	}

	/**
	 * Adds are not supported, this is a read only data set.
	 * @param inputData Not used.
	 * @param idealData Not used.
	 */
	public void add(final NeuralData inputData, final NeuralData idealData) {
		throw new NeuralDataError(XMLNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	/**
	 * Adds are not supported, this is a read only data set.
	 * @param inputData Not used.
	 */
	public void add(final NeuralDataPair inputData) {
		throw new NeuralDataError(XMLNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	/**
	 * Adds are not supported, this is a read only data set.
	 * @param data1 Not used.
	 */
	public void add(final NeuralData data1) {
		throw new NeuralDataError(XMLNeuralDataSet.ADD_NOT_SUPPORTED);
	}
}
