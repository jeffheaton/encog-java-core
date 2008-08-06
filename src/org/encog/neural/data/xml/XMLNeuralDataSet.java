package org.encog.neural.data.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.encog.bot.html.ParseHTML;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.NeuralNetworkException;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.data.csv.CSVNeuralDataSet;
import org.encog.neural.persist.persistors.BasicNeuralDataSetPersistor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLNeuralDataSet extends BasicNeuralDataSet {

	public final static String ADD_NOT_SUPPORTED = "Adds are not supported with this dataset, it is read only.";
		
	public XMLNeuralDataSet(
			String filename,
			String pairXML,
			String inputXML,
			String idealXML,
			String valueXML) throws NeuralNetworkException
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
			throw new NeuralNetworkException(e);
		} catch (org.xml.sax.SAXException e) {
			throw new NeuralNetworkException(e);
		} catch (java.io.IOException e) {
			throw new NeuralNetworkException(e);
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
