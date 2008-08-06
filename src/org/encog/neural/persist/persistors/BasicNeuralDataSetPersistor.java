package org.encog.neural.persist.persistors;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.encog.neural.NeuralNetworkException;
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
	
	@Override
	public EncogPersistedObject load(Element pairs)
			throws NeuralNetworkException {
		
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
			Element node = (Element) child;
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

	@Override
	public void save(EncogPersistedObject object, TransformerHandler hd)
			throws NeuralNetworkException {
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
			throw new NeuralNetworkException(e);
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
