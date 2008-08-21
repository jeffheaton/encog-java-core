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
package org.encog.neural.data.csv;

import java.io.IOException;
import java.util.Iterator;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.util.ReadCSV;

public class CSVNeuralDataSet implements NeuralDataSet {
	
	public final static String ADD_NOT_SUPPORTED = "Adds are not supported with this dataset, it is read only.";
	
	private String filename;
	private int inputSize;
	private int idealSize;
	private char delimiter;
	private boolean headers;
	
	public class CSVNeuralIterator implements Iterator<NeuralDataPair>
	{
		private ReadCSV reader;
		private boolean dataReady;
		
		public CSVNeuralIterator()
		{
			try {
				this.reader = null;
				this.reader = new ReadCSV(filename,headers,delimiter);
				dataReady = false;
			} catch (IOException e) {
				throw new NeuralNetworkError(e);
			}
		}
		
		
		public boolean hasNext() {
			if( this.reader==null )
				return false;
			
			if( dataReady )
				return true;
			
			try
			{
				if( reader.next() )
				{
					dataReady = true;
					return true;
				}
				else 
				{
					dataReady = false;
					return false;
				}
			}
			catch(IOException e)
			{
				throw new NeuralNetworkError(e);
			}
			
		}

		public NeuralDataPair next()  {
						
			NeuralData input = new BasicNeuralData(inputSize);
			NeuralData ideal = null;
			
			for(int i=0;i<inputSize;i++)
			{
				input.setData(i, reader.getDouble(i));
			}
			
			if( idealSize>0 )
			{
				ideal = new BasicNeuralData(idealSize);
				for(int i=0;i<idealSize;i++)
				{
					ideal.setData(i, reader.getDouble(i+inputSize));
				}
				 
			}
			
			this.dataReady = false;		
			return new BasicNeuralDataPair(input,ideal);
		}

		public void remove() {
			try {
				this.reader.close();
			} catch (IOException e) {
				// Not much we can do at this point, and throwing will
				// break the interface.
			}
			
		}	
	}
	
	public CSVNeuralDataSet(String filename, int inputSize,int idealSize,boolean headers)
	{
		this(filename,inputSize,idealSize,headers,',');
	}
	
	public CSVNeuralDataSet(String filename, int inputSize,int idealSize,boolean headers,char delimiter)
	{
		this.filename = filename;
		this.inputSize = inputSize;
		this.idealSize = idealSize;
		this.delimiter = delimiter;
		this.headers = headers;
	}

	public void add(NeuralData inputData, NeuralData idealData) {
		throw new NeuralDataError(CSVNeuralDataSet.ADD_NOT_SUPPORTED);
		
	}

	public void add(NeuralDataPair inputData) {
		throw new NeuralDataError(CSVNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	public int getIdealSize() {
		return this.idealSize;
	}

	public int getInputSize() {
		return this.inputSize;
	}

	public Iterator<NeuralDataPair> iterator() {
		return new CSVNeuralIterator();
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the delimiter
	 */
	public char getDelimiter() {
		return delimiter;
	}

	public void add(NeuralData data1) {
		throw new NeuralDataError(CSVNeuralDataSet.ADD_NOT_SUPPORTED);		
	}
}
