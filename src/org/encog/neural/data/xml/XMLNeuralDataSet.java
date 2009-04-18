/*
 * Encog Artificial Intelligence Framework v2.x
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.data.csv.CSVNeuralDataSet.CSVNeuralIterator;
import org.encog.parse.tags.read.ReadXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data source that reads XML files.  This class is not memory based, so
 * very large XML files can be used, without problem.
 */
public class XMLNeuralDataSet implements NeuralDataSet {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -5960796361565902008L;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Error Message: adds are not supported.
	 */
	public static final String ADD_NOT_SUPPORTED = 
		"Adds are not supported with this dataset, it is read only.";
	
	private final String filename; 
	private final String pairXML;
	private final String inputXML;
	private final String idealXML;
	private final String valueXML;
	private final int inputSize;
	private final int idealSize;
	/**
	 * A collection of iterators that have been created.
	 */
	private final List<XMLNeuralIterator> iterators = 
		new ArrayList<XMLNeuralIterator>();
	
	/**
	 * An iterator designed to read from XML files.
	 * @author jheaton
	 */
	public class XMLNeuralIterator implements Iterator<NeuralDataPair> {

		private InputStream file;
		private ReadXML reader;
		private NeuralDataPair nextPair;
		
		public XMLNeuralIterator()
		{
			try
			{
				this.file = new FileInputStream(getFilename());
				this.reader = new ReadXML(this.file);
			}
			catch(IOException e)
			{
				if( logger.isErrorEnabled() )
				{
					logger.error("Exception",e);
				}
				throw new NeuralNetworkError(e);
			}
		}
		
		public boolean hasNext() {
			if( this.nextPair!=null)
				return true;
			
			return obtainNext();
		}
		
		private boolean obtainNext()
		{
			if( !this.reader.findTag(getPairXML(),true) )
				return false;
			
			NeuralData input = new BasicNeuralData(inputSize);
			NeuralData ideal = new BasicNeuralData(idealSize);
			
			if( !this.reader.findTag(getInputXML(),true) )
				invalidError();
			
			for(int i=0;i<inputSize;i++)
			{
				if( !this.reader.findTag(getValueXML(),true) )
					invalidError();
				String str = this.reader.readTextToTag();
				input.setData(i,Double.parseDouble(str));
			}
			
			if( idealSize>0 )
			{
				if( !this.reader.findTag(getIdealXML(),true) )
					invalidError();
				
				for(int i=0;i<idealSize;i++)
				{
					if( !this.reader.findTag(getValueXML(),true) )
						invalidError();
					String str = this.reader.readTextToTag();
					ideal.setData(i,Double.parseDouble(str));
				}
			}
			
			if( ideal!=null )
			{
				this.nextPair = new BasicNeuralDataPair(input,ideal);
			}
			else
			{
				this.nextPair = new BasicNeuralDataPair(input);
			}
			
			return true;
		}
		
		private void invalidError()
		{
			String str = "Could not parse XML, incons;istant tag structure.";
			if( logger.isErrorEnabled())
			{
				logger.error(str);
			}
			throw new NeuralNetworkError(str);
		}

		public NeuralDataPair next() {
			NeuralDataPair result = this.nextPair;
			
			if( result==null )
			{
				if( !obtainNext() )
					return null;
				result = this.nextPair;
			}
			
			this.nextPair = null;
			return result;
		}

		public void remove() {
			try
			{
				this.file.close();
				iterators.remove(this);
			}
			catch(IOException e)
			{
				if( logger.isErrorEnabled())
				{
					logger.error("Error",e);
				}
				throw new NeuralNetworkError(e);
			}
			
		}		
	}
	

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
			final int inputSize,
			final int idealSize,
			final String pairXML, 
			final String inputXML,
			final String idealXML, 
			final String valueXML) {
		this.filename = filename;
		this.pairXML = pairXML;
		this.inputXML = inputXML;
		this.idealXML = idealXML;
		this.valueXML = valueXML;
		this.idealSize = idealSize;
		this.inputSize = inputSize;
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

	public String getFilename() {
		return filename;
	}

	public String getPairXML() {
		return pairXML;
	}

	public String getInputXML() {
		return inputXML;
	}

	public String getIdealXML() {
		return idealXML;
	}

	public String getValueXML() {
		return valueXML;
	}

	public void close() {
		for(int i=0;i<this.iterators.size();i++)
		{
			XMLNeuralIterator iterator = this.iterators.get(i);
			iterator.remove();
		}		
	}

	public int getIdealSize() {
		return this.idealSize;
	}

	public int getInputSize() {
		return this.inputSize;
	}

	public Iterator<NeuralDataPair> iterator() {
		XMLNeuralIterator result = new XMLNeuralIterator();
		this.iterators.add(result);
		return result;
	}
	
	
}
