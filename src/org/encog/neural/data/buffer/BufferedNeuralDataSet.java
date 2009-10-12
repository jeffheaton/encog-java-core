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
package org.encog.neural.data.buffer;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;

/**
 * This class is not memory based, so very long files can be used, without
 * running out of memory. This dataset uses a binary file as a buffer. When used
 * with a slower access dataset, such as CSV, XML or SQL, where parsing must
 * occur, this dataset can be used to load from the slower dataset and train at
 * much higher speeds.
 * 
 * If you are going to create a binary file, by using the add methods, you must
 * call beginLoad to cause Encog to open an output file. Once the data has been
 * loaded, call endLoad.
 * 
 * The floating point numbers stored to the binary file may not be cross
 * platform.
 */
public class BufferedNeuralDataSet implements NeuralDataSet, Indexable {

	/**
	 * An iterator to move through the buffered data set.
	 */
	public class BufferedNeuralDataSetIterator implements
			Iterator<NeuralDataPair> {

		/**
		 * The file to read from.
		 */
		private RandomAccessFile input;

		/**
		 * The next data pair to read.
		 */
		private NeuralDataPair next;

		/**
		 * The data pair that was just read.
		 */
		private NeuralDataPair current;

		/**
		 * Is there data ready to return.
		 */
		private boolean dataReady;

		/**
		 * Construct the buffered iterator. This is where the file is actually
		 * opened.
		 */
		public BufferedNeuralDataSetIterator() {
			try {
				this.input = new RandomAccessFile(
						BufferedNeuralDataSet.this.bufferFile, "r");
				this.input.readLong();
				this.input.readLong();

				this.next = createPair();
				this.current = createPair();

				readNext();
			} catch (final IOException e) {
				throw new NeuralDataError(e);
			}
		}

		/**
		 * Close the iterator, and the underlying file.
		 */
		public void close() {
			try {
				this.input.close();
			} catch (final IOException e) {
				throw new NeuralDataError(e);
			}
		}

		/**
		 * Create a neural data pair of the correct size.
		 */
		private NeuralDataPair createPair() {
			NeuralDataPair result;

			if (BufferedNeuralDataSet.this.idealSize > 0) {
				result = new BasicNeuralDataPair(new BasicNeuralData(
						(int) BufferedNeuralDataSet.this.inputSize),
						new BasicNeuralData(
								(int) BufferedNeuralDataSet.this.idealSize));
			} else {
				result = new BasicNeuralDataPair(new BasicNeuralData(
						(int) BufferedNeuralDataSet.this.inputSize));
			}

			return result;
		}

		/**
		 * @return True if there is more data to read.
		 */
		public boolean hasNext() {
			if (this.dataReady == false) {
				readNext();
			}
			return this.dataReady;
		}

		/**
		 * Read the next pair.
		 */
		public NeuralDataPair next() {

			if (!this.dataReady) {
				readNext();
			}

			if (!this.dataReady) {
				return null;
			}

			// swap
			final NeuralDataPair temp = this.current;
			this.current = this.next;
			this.next = temp;
			readNext();

			return this.current;
		}		

		/**
		 * Read the next pair.
		 */
		private void readNext() {
			try {
				if (BufferedNeuralDataSet.this.idealSize > 0) {
					readDoubleArray(this.next.getInput());
					readDoubleArray(this.next.getIdeal());
				} else {
					readDoubleArray(this.next.getInput());
				}

				this.dataReady = true;
			} catch (final EOFException e) {
				this.dataReady = false;
			} catch (final IOException e) {
				throw new NeuralDataError(e);
			}
		}

		/**
		 * Not supported, will throw an error.
		 */
		public void remove() {
			throw new NeuralDataError(BufferedNeuralDataSet.ERROR_REMOVE);
		}

	}

	/**
	 * Error message for ADD.
	 */
	public static final String ERROR_ADD = "Add can only be used after calling beginLoad.";

	/**
	 * Error message for REMOVE.
	 */
	public static final String ERROR_REMOVE = "Remove is not supported for BufferedNeuralDataSet.";

	/**
	 * The buffer file to use.
	 */
	private final File bufferFile;

	/**
	 * The size of the input data.
	 */
	private long inputSize;

	/**
	 * The size of the ideal data.
	 */
	private long idealSize;
	
	/**
	 * The size(in bytes) of a record.
	 */
	private int recordSize;

	/**
	 * The iterators.
	 */
	private final Collection<BufferedNeuralDataSetIterator> iterators = new ArrayList<BufferedNeuralDataSetIterator>();

	/**
	 * A random access file to use for output.
	 */
	private RandomAccessFile output;

	private RandomAccessFile input;

	/**
	 * Construct a buffered dataset using the specified file.
	 * 
	 * @param bufferFile
	 *            The file to read/write binary data to/from.
	 */
	public BufferedNeuralDataSet(final File bufferFile) {
		this.bufferFile = bufferFile;
		try {
			if (bufferFile.exists()) {
				final RandomAccessFile out = new RandomAccessFile(
						this.bufferFile, "rw");
				this.inputSize = out.readLong();
				this.idealSize = out.readLong();
				this.recordSize = (this.getInputSize() * 8) + (this.getIdealSize() * 8);
				out.close();
			}
		} catch (final IOException e) {
			throw new NeuralDataError(e);
		}
	}

	/**
	 * Add only input data, for an unsupervised dataset.
	 * 
	 * @param data1
	 *            The data to be added.
	 */
	public void add(final NeuralData data1) {
		if (this.output == null) {
			throw new NeuralDataError(BufferedNeuralDataSet.ERROR_ADD);
		}
		writeDoubleArray(data1);

	}

	/**
	 * Add both the input and ideal data.
	 * 
	 * @param inputData
	 *            The input data.
	 * @param idealData
	 *            The ideal data.
	 */
	public void add(final NeuralData inputData, final NeuralData idealData) {
		if (this.output == null) {
			throw new NeuralDataError(BufferedNeuralDataSet.ERROR_ADD);
		}
		writeDoubleArray(inputData);
		writeDoubleArray(idealData);
	}

	/**
	 * Add a data pair of both input and ideal data.
	 * 
	 * @param inputData
	 *            The pair to add.
	 */
	public void add(final NeuralDataPair inputData) {
		if (this.output == null) {
			throw new NeuralDataError(BufferedNeuralDataSet.ERROR_ADD);
		}
		writeDoubleArray(inputData.getInput());
		if (inputData.getIdeal() != null) {
			writeDoubleArray(inputData.getIdeal());
		}
	}

	/**
	 * Begin loading to the binary file. After calling this method the add
	 * methods may be called.
	 * 
	 * @param inputSize
	 *            The input size.
	 * @param idealSize
	 *            The ideal size.
	 */
	public void beginLoad(final int inputSize, final int idealSize) {
		try {
			this.inputSize = inputSize;
			this.idealSize = idealSize;
			this.recordSize = (this.getInputSize() * 8) + (this.getIdealSize() * 8);
			this.bufferFile.delete();
			this.output = new RandomAccessFile(this.bufferFile, "rw");
			// write the header
			this.output.writeLong(this.inputSize);
			this.output.writeLong(this.idealSize);
		} catch (final IOException e) {
			throw new NeuralDataError(e);
		}

	}

	/**
	 * Close all iterators.
	 */
	public void close() {
		for (final BufferedNeuralDataSetIterator iterator : this.iterators) {
			iterator.close();
		}

	}

	/**
	 * This method should be called once all the data has been loaded. The
	 * underlying file will be closed.
	 */
	public void endLoad() {
		try {
			this.output.close();
			this.output = null;
		} catch (final IOException e) {
			throw new NeuralDataError(e);
		}
	}

	/**
	 * @return Get the ideal data size.
	 */
	public int getIdealSize() {
		return (int) this.idealSize;
	}

	/**
	 * @return Get the input data size.
	 */
	public int getInputSize() {
		return (int) this.inputSize;
	}

	/**
	 * Create an iterator.
	 */
	public BufferedNeuralDataSetIterator iterator() {
		if (this.output != null) {
			throw new NeuralDataError(
					"Can't create iterator while loading, call endLoad first.");
		}
		final BufferedNeuralDataSetIterator result = new BufferedNeuralDataSetIterator();
		this.iterators.add(result);
		return result;
	}

	/**
	 * Load from the specified data source into the binary file. Do not call
	 * beginLoad before calling this method, as this is handled internally.
	 * 
	 * @param source
	 *            The source.
	 */
	public void load(final NeuralDataSet source) {
		beginLoad(source.getInputSize(), source.getIdealSize());

		// write the data
		for (final NeuralDataPair pair : source) {
			if (pair.getInput() != null) {
				writeDoubleArray(pair.getInput());
			}
			if (pair.getIdeal() != null) {
				writeDoubleArray(pair.getIdeal());
			}
		}

		endLoad();
	}

	/**
	 * Write a double array from the specified data to the file.
	 * 
	 * @param data
	 *            The data that holds the array.
	 */
	private void writeDoubleArray(final NeuralData data) {
		try {
			for (int i = 0; i < data.size(); i++) {
				this.output.writeDouble(data.getData(i));
			}
		} catch (final IOException e) {
			throw new NeuralDataError(e);
		}
	}

	private void openInputFile() {

		try {
			if (this.input == null) {
				this.input = new RandomAccessFile(bufferFile, "r");
			}
		} catch (IOException e) {
			throw new NeuralDataError(e);
		}
	}
	
	/**
	 * Read an array of doubles from the file.
	 * 
	 * @param data
	 *            The neural data to read this array into.
	 * @throws EOFException
	 *             End of file reached.
	 * @throws IOException
	 *             Error reading data.
	 */
	private void readDoubleArray(final NeuralData data)
			throws EOFException, IOException {
		final double[] d = data.getData();
		for (int i = 0; i < data.size(); i++) {
			d[i] = this.input.readDouble();
		}
	}

	
	public void getRecord(int index, NeuralDataPair pair) {
		try
		{
			openInputFile();			
			this.input.seek(index*this.recordSize);
			if (BufferedNeuralDataSet.this.idealSize > 0) {
				readDoubleArray(pair.getInput());
				readDoubleArray(pair.getIdeal());
			} else {
				readDoubleArray(pair.getInput());
			}
		}
		catch(IOException e)
		{
			throw new NeuralDataError(e); 
		}
	}

	
	public long getRecordCount() {
		openInputFile();
		return this.bufferFile.length() / this.recordSize;
	}

}
