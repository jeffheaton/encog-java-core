package org.encog.neural.data.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.encog.neural.NeuralNetworkException;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLNeuralDataSet implements NeuralDataSet {

	public class XMLNeuralDataSetIterator implements Iterator<NeuralDataPair>
	{
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		public NeuralDataPair next() {
			// TODO Auto-generated method stub
			return null;
		}

		public void remove() {
			// TODO Auto-generated method stub
			
		}		
	}
	
	public void add(NeuralData inputData, NeuralData idealData) {
		// TODO Auto-generated method stub
		
	}

	public void add(NeuralDataPair inputData) {
		// TODO Auto-generated method stub
		
	}

	public int getIdealSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInputSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Iterator<NeuralDataPair> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void load(String filename) throws NeuralNetworkException 
	{
		try
		{
		InputStream fis = new FileInputStream(filename);
		load(fis);
		fis.close();
		}
		catch(IOException e)
		{
			throw new NeuralNetworkException(e);
		}
	}
	
	public void load(InputStream is) throws NeuralNetworkException 
	{
		try
		{
		
		XMLReader xr = XMLReaderFactory.createXMLReader();
		SAXHandler handler = new SAXHandler();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);

		xr.parse(new InputSource(is));
		}
		catch(SAXException e)
		{
			throw new NeuralNetworkException(e);
		} catch (IOException e) {
			throw new NeuralNetworkException(e);
		}
			
	}

}
