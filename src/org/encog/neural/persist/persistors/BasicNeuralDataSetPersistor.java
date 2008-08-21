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
import org.encog.neural.persist.EncogPersistedObject;
import org.encog.neural.persist.Persistor;
import org.encog.util.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class BasicNeuralDataSetPersistor implements Persistor {

	private String pairXML;
	private String inputXML;
	private String idealXML;
	private String valueXML;
	
	
	public BasicNeuralDataSetPersistor()
	{
		this.pairXML = "NeuralDataPair";
		this.inputXML = "input";
		this.idealXML = "ideal";
		this.valueXML = "value";
	}
	
	public EncogPersistedObject load(Element pairs)
			throws NeuralNetworkError {
		
		BasicNeuralDataSet result = new BasicNeuralDataSet();
		
		for (Node child = pairs.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (!(child instanceof Element))
				continue;
			Element node = (Element) child;
			if( child.getNodeName().equals(this.pairXML))
			{
				NeuralDataPair pair = loadPair(node);
				result.add(pair);
			}
		}
		return result;
	}
	
	private NeuralDataPair loadPair(Element node)
	{
		Element inputNode = XMLUtil.findElement(node, this.inputXML);
		Element idealNode = XMLUtil.findElement(node, this.idealXML);
		NeuralData input = loadValues(inputNode);
		NeuralData ideal = loadValues(idealNode);
		return new BasicNeuralDataPair(input,ideal);
	}

	private NeuralData loadValues(Element inputNode) {
		List<Double> list = new ArrayList<Double>();
		
		for (Node child = inputNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (!(child instanceof Element))
				continue;

			if( child.getNodeName().equals(this.valueXML))
			{
				String str = child.getTextContent();
				double d = Double.parseDouble(str);
				list.add(d);
			}
		}
		
		int i=0;
		NeuralData result = new BasicNeuralData(list.size());
		for( double d: list)
		{
			result.setData(i++,d);
		}
		
		return result;
		
	}

	public void save(EncogPersistedObject object, TransformerHandler hd)
			throws NeuralNetworkError {
		try {
			BasicNeuralDataSet set = (BasicNeuralDataSet) object;

			AttributesImpl atts = new AttributesImpl();
			hd.startElement("", "", object.getClass().getSimpleName(), atts);

			for(NeuralDataPair pair: set)
			{
				hd.startElement("", "", this.pairXML, atts);
				hd.startElement("", "", this.inputXML, atts);
				for( int i=0;i<pair.getInput().size();i++)
				{
					hd.startElement("", "", this.valueXML, atts);
					String data = ""+pair.getInput().getData(i);
					hd.characters(data.toCharArray(), 0, data.length());
					hd.endElement("", "", this.valueXML);					
				}
				hd.endElement("", "", this.inputXML);
				hd.startElement("", "", this.idealXML, atts);
				for( int i=0;i<pair.getIdeal().size();i++)
				{
					hd.startElement("", "", this.valueXML, atts);
					String data = ""+pair.getIdeal().getData(i);
					hd.characters(data.toCharArray(), 0, data.length());
					hd.endElement("", "", this.valueXML);					
				}				
				hd.endElement("", "", this.idealXML);
				hd.endElement("", "", this.pairXML);
			}

			hd.endElement("", "", object.getClass().getSimpleName());
		} catch (SAXException e) {
			throw new NeuralNetworkError(e);
		}
		
	}

	/**
	 * @return the pairXML
	 */
	public String getPairXML() {
		return pairXML;
	}

	/**
	 * @param pairXML the pairXML to set
	 */
	public void setPairXML(String pairXML) {
		this.pairXML = pairXML;
	}

	/**
	 * @return the inputXML
	 */
	public String getInputXML() {
		return inputXML;
	}

	/**
	 * @param inputXML the inputXML to set
	 */
	public void setInputXML(String inputXML) {
		this.inputXML = inputXML;
	}

	/**
	 * @return the idealXML
	 */
	public String getIdealXML() {
		return idealXML;
	}

	/**
	 * @param idealXML the idealXML to set
	 */
	public void setIdealXML(String idealXML) {
		this.idealXML = idealXML;
	}

	/**
	 * @return the valueXML
	 */
	public String getValueXML() {
		return valueXML;
	}

	/**
	 * @param valueXML the valueXML to set
	 */
	public void setValueXML(String valueXML) {
		this.valueXML = valueXML;
	}

}
