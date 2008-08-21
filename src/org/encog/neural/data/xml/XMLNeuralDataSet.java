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
import org.encog.neural.persist.persistors.BasicNeuralDataSetPersistor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLNeuralDataSet extends BasicNeuralDataSet {

	public final static String ADD_NOT_SUPPORTED = "Adds are not supported with this dataset, it is read only.";
		
	public XMLNeuralDataSet(
			String filename,
			String pairXML,
			String inputXML,
			String idealXML,
			String valueXML)
	{
				
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

			BasicNeuralDataSetPersistor persistor = new BasicNeuralDataSetPersistor();
			
			persistor.setIdealXML(idealXML);
			persistor.setInputXML(inputXML);
			persistor.setPairXML(pairXML);
			persistor.setValueXML(valueXML);
			
			BasicNeuralDataSet set = (BasicNeuralDataSet) persistor.load(node);
			this.setData(set.getData());

			
			is.close();

		} catch (javax.xml.parsers.ParserConfigurationException e) {
			throw new NeuralNetworkError(e);
		} catch (org.xml.sax.SAXException e) {
			throw new NeuralNetworkError(e);
		} catch (java.io.IOException e) {
			throw new NeuralNetworkError(e);
		}	
	}
	
	public void add(NeuralData inputData, NeuralData idealData) {
		throw new NeuralDataError(XMLNeuralDataSet.ADD_NOT_SUPPORTED);		
	}

	public void add(NeuralDataPair inputData) {
		throw new NeuralDataError(XMLNeuralDataSet.ADD_NOT_SUPPORTED);		
	}
	
	public void add(NeuralData data1) {
		throw new NeuralDataError(XMLNeuralDataSet.ADD_NOT_SUPPORTED);		
	}
}
