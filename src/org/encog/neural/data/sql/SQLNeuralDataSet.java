package org.encog.neural.data.sql;

import java.io.IOException;
import java.sql.Connection;
import java.util.Iterator;

import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.util.ReadCSV;

public class SQLNeuralDataSet implements NeuralDataSet {
	
	public final static String ADD_NOT_SUPPORTED = "Adds are not supported with this dataset, it is read only.";
	
	private String filename;
	private int inputSize;
	private int idealSize;
	private char delimiter;
	private boolean headers;
	
	public class SQLNeuralIterator implements Iterator<NeuralDataPair>
	{
		private ReadCSV reader;
		private boolean dataReady;
		
		public SQLNeuralIterator()
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
	
	public SQLNeuralDataSet(String filename, int inputSize,int idealSize)
	{
		this(filename,inputSize,idealSize,',');
	}
	
	public SQLNeuralDataSet(String filename, int inputSize,int idealSize,char delimiter)
	{
		this.filename = filename;
		this.inputSize = inputSize;
		this.idealSize = idealSize;
		this.delimiter = delimiter;
		this.headers = headers;
	}

	public void add(NeuralData inputData, NeuralData idealData) {
		throw new NeuralDataError(SQLNeuralDataSet.ADD_NOT_SUPPORTED);
		
	}

	public void add(NeuralDataPair inputData) {
		throw new NeuralDataError(SQLNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	public int getIdealSize() {
		return this.idealSize;
	}

	public int getInputSize() {
		return this.inputSize;
	}

	public Iterator<NeuralDataPair> iterator() {
		return new SQLNeuralIterator();
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
		throw new NeuralDataError(SQLNeuralDataSet.ADD_NOT_SUPPORTED);		
	}

}
