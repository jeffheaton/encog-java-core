package org.encog.neural.data.csv;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.util.ReadCSV;

public class CSVNeuralDataSet implements NeuralDataSet {
	
	public final static String ADD_NOT_SUPPORTED = "Adds are not supported with this dataset, it is read only.";
	
	private String filename;
	private int inputSize;
	private int idealSize;
	private char delimiter;
	
	public class CSVNeuralIterator implements Iterator<NeuralDataPair>
	{
		private ReadCSV reader;
		
		public CSVNeuralIterator()
		{
			try {
				this.reader = null;
				this.reader = new ReadCSV(filename);
			} catch (IOException e) {
				// if this happens just allow reader to remain null
			}
		}
		
		@Override
		public boolean hasNext() {
			if( this.reader==null )
				return false;
			return true;
			
		}

		@Override
		public NeuralDataPair next() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}	
	}
	
	public CSVNeuralDataSet(String filename, int inputSize,int idealSize,char delimiter)
	{
		this.filename = filename;
		this.inputSize = inputSize;
		this.idealSize = idealSize;
		this.delimiter = delimiter;
	}

	@Override
	public void add(NeuralData inputData, NeuralData idealData) {
		throw new NeuralDataError(CSVNeuralDataSet.ADD_NOT_SUPPORTED);
		
	}

	@Override
	public void add(NeuralDataPair inputData) {
		throw new NeuralDataError(CSVNeuralDataSet.ADD_NOT_SUPPORTED);
	}

	@Override
	public int getIdealSize() {
		return this.idealSize;
	}

	@Override
	public int getInputSize() {
		return this.inputSize;
	}

	@Override
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

	@Override
	public void add(NeuralData data1) {
		// TODO Auto-generated method stub
		
	}
}
