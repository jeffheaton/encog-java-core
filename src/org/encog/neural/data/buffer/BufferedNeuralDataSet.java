package org.encog.neural.data.buffer;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;

/**
 * This class is not memory based, so very long files can be used, without
 * running out of memory.
 */
public class BufferedNeuralDataSet implements NeuralDataSet {

	private File bufferFile;
	private long inputSize;
	private long idealSize;
	private Collection<BufferedNeuralDataSetIterator> iterators = new ArrayList<BufferedNeuralDataSetIterator>();
	private RandomAccessFile output;

	public static final String ERROR_ADD = "Add can only be used after calling beginLoad.";
	public static final String ERROR_REMOVE = "Remove is not supported for BufferedNeuralDataSet.";

	public class BufferedNeuralDataSetIterator implements
			Iterator<NeuralDataPair> {
		private RandomAccessFile input;
		private boolean eof;
		private NeuralDataPair next;
		private NeuralDataPair current;
		private boolean dataReady;

		public BufferedNeuralDataSetIterator() {
			try {
				this.input = new RandomAccessFile(bufferFile, "r");
				this.input.readLong();
				this.input.readLong();
				this.eof = false;

				this.next = createPair();
				this.current = createPair();

				readNext();
			} catch (IOException e) {
				throw new NeuralDataError(e);
			}
		}

		private NeuralDataPair createPair() {
			NeuralDataPair result;

			if (idealSize > 0) {
				result = new BasicNeuralDataPair(new BasicNeuralData(
						(int) inputSize), new BasicNeuralData((int) idealSize));
			} else {
				result = new BasicNeuralDataPair(new BasicNeuralData(
						(int) inputSize));
			}

			return result;
		}

		public boolean hasNext() {
			if (this.dataReady == false) {
				readNext();
			}
			return this.dataReady;
		}

		private void readDoubleArray(NeuralData data) throws EOFException,
				IOException {
			double[] d = data.getData();
			for (int i = 0; i < data.size(); i++) {
				d[i] = this.input.readDouble();
			}
		}

		public NeuralDataPair next() {

			if (!dataReady) {
				readNext();
			}

			if (!dataReady) {
				return null;
			}

			// swap
			NeuralDataPair temp = this.current;
			this.current = this.next;
			this.next = temp;
			readNext();

			return this.current;
		}

		private void readNext() {
			try {
				if (idealSize > 0) {
					readDoubleArray(next.getInput());
					readDoubleArray(next.getIdeal());
				} else {
					readDoubleArray(next.getInput());
				}

				this.dataReady = true;
			} catch (EOFException e) {
				this.eof = true;
				this.dataReady = false;
			} catch (IOException e) {
				throw new NeuralDataError(e);
			}
		}

		public void remove() {
			throw new NeuralDataError(BufferedNeuralDataSet.ERROR_REMOVE);
		}

		public void close() {
			try {
				this.input.close();
			} catch (IOException e) {
				throw new NeuralDataError(e);
			}
		}

	}

	public BufferedNeuralDataSet(File bufferFile) {
		this.bufferFile = bufferFile;
		try {
			if (bufferFile.exists()) {
				RandomAccessFile out = new RandomAccessFile(this.bufferFile,
						"rw");
				this.inputSize = out.readLong();
				this.idealSize = out.readLong();
				out.close();
			}
		} catch (IOException e) {
			throw new NeuralDataError(e);
		}
	}

	public void beginLoad(int inputSize, int idealSize) {
		try {
			this.inputSize = inputSize;
			this.idealSize = idealSize;

			this.bufferFile.delete();
			this.output = new RandomAccessFile(this.bufferFile, "rw");
			// write the header
			this.output.writeLong(this.inputSize);
			this.output.writeLong(this.idealSize);
		} catch (IOException e) {
			throw new NeuralDataError(e);
		}

	}

	public void endLoad() {
		try {
			this.output.close();
			this.output = null;
		} catch (IOException e) {
			throw new NeuralDataError(e);
		}
	}

	public void load(NeuralDataSet source) {
		beginLoad(source.getInputSize(), source.getIdealSize());

		// write the data
		for (NeuralDataPair pair : source) {
			if (pair.getInput() != null) {
				writeDoubleArray(pair.getInput());
			}
			if (pair.getIdeal() != null) {
				writeDoubleArray(pair.getIdeal());
			}
		}

		endLoad();
	}

	private void writeDoubleArray(NeuralData data) {
		try {
			for (int i = 0; i < data.size(); i++) {
				output.writeDouble(data.getData(i));
			}
		} catch (IOException e) {
			throw new NeuralDataError(e);
		}
	}

	public void add(NeuralData data1) {
		if (output == null) {
			throw new NeuralDataError(BufferedNeuralDataSet.ERROR_ADD);
		}
		writeDoubleArray(data1);

	}

	public void add(NeuralData inputData, NeuralData idealData) {
		if (output == null) {
			throw new NeuralDataError(BufferedNeuralDataSet.ERROR_ADD);
		}
		writeDoubleArray(inputData);
		writeDoubleArray(idealData);
	}

	public void add(NeuralDataPair inputData) {
		if (output == null) {
			throw new NeuralDataError(BufferedNeuralDataSet.ERROR_ADD);
		}
		writeDoubleArray(inputData.getInput());
		if (inputData.getIdeal() != null)
			writeDoubleArray(inputData.getIdeal());
	}

	public void close() {
		for (BufferedNeuralDataSetIterator iterator : this.iterators) {
			iterator.close();
		}

	}

	public int getIdealSize() {
		return (int) this.idealSize;
	}

	public int getInputSize() {
		return (int) this.inputSize;
	}

	public BufferedNeuralDataSetIterator iterator() {
		if (this.output != null) {
			throw new NeuralDataError(
					"Can't create iterator while loading, call endLoad first.");
		}
		BufferedNeuralDataSetIterator result = new BufferedNeuralDataSetIterator();
		this.iterators.add(result);
		return result;
	}

}
