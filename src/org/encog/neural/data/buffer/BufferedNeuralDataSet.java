package org.encog.neural.data.buffer;

import java.io.EOFException;
import java.io.File;
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

	public class BufferedNeuralDataSetIterator implements
			Iterator<NeuralDataPair> {
		private RandomAccessFile input;
		private boolean eof;
		private NeuralDataPair next;
		private NeuralDataPair current;
		private boolean dataReady;

		public BufferedNeuralDataSetIterator() {
			try {
				this.input = new RandomAccessFile(
						BufferedNeuralDataSet.this.bufferFile, "r");
				this.input.readLong();
				this.input.readLong();
				this.eof = false;

				this.next = createPair();
				this.current = createPair();

				readNext();
			} catch (final IOException e) {
				throw new NeuralDataError(e);
			}
		}

		public void close() {
			try {
				this.input.close();
			} catch (final IOException e) {
				throw new NeuralDataError(e);
			}
		}

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

		public boolean hasNext() {
			if (this.dataReady == false) {
				readNext();
			}
			return this.dataReady;
		}

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

		private void readDoubleArray(final NeuralData data)
				throws EOFException, IOException {
			final double[] d = data.getData();
			for (int i = 0; i < data.size(); i++) {
				d[i] = this.input.readDouble();
			}
		}

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
				this.eof = true;
				this.dataReady = false;
			} catch (final IOException e) {
				throw new NeuralDataError(e);
			}
		}

		public void remove() {
			throw new NeuralDataError(BufferedNeuralDataSet.ERROR_REMOVE);
		}

	}

	public static final String ERROR_ADD = "Add can only be used after calling beginLoad.";
	public static final String ERROR_REMOVE = "Remove is not supported for BufferedNeuralDataSet.";
	private final File bufferFile;
	private long inputSize;

	private long idealSize;
	private final Collection<BufferedNeuralDataSetIterator> iterators = new ArrayList<BufferedNeuralDataSetIterator>();

	private RandomAccessFile output;

	public BufferedNeuralDataSet(final File bufferFile) {
		this.bufferFile = bufferFile;
		try {
			if (bufferFile.exists()) {
				final RandomAccessFile out = new RandomAccessFile(
						this.bufferFile, "rw");
				this.inputSize = out.readLong();
				this.idealSize = out.readLong();
				out.close();
			}
		} catch (final IOException e) {
			throw new NeuralDataError(e);
		}
	}

	public void add(final NeuralData data1) {
		if (this.output == null) {
			throw new NeuralDataError(BufferedNeuralDataSet.ERROR_ADD);
		}
		writeDoubleArray(data1);

	}

	public void add(final NeuralData inputData, final NeuralData idealData) {
		if (this.output == null) {
			throw new NeuralDataError(BufferedNeuralDataSet.ERROR_ADD);
		}
		writeDoubleArray(inputData);
		writeDoubleArray(idealData);
	}

	public void add(final NeuralDataPair inputData) {
		if (this.output == null) {
			throw new NeuralDataError(BufferedNeuralDataSet.ERROR_ADD);
		}
		writeDoubleArray(inputData.getInput());
		if (inputData.getIdeal() != null) {
			writeDoubleArray(inputData.getIdeal());
		}
	}

	public void beginLoad(final int inputSize, final int idealSize) {
		try {
			this.inputSize = inputSize;
			this.idealSize = idealSize;

			this.bufferFile.delete();
			this.output = new RandomAccessFile(this.bufferFile, "rw");
			// write the header
			this.output.writeLong(this.inputSize);
			this.output.writeLong(this.idealSize);
		} catch (final IOException e) {
			throw new NeuralDataError(e);
		}

	}

	public void close() {
		for (final BufferedNeuralDataSetIterator iterator : this.iterators) {
			iterator.close();
		}

	}

	public void endLoad() {
		try {
			this.output.close();
			this.output = null;
		} catch (final IOException e) {
			throw new NeuralDataError(e);
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
		final BufferedNeuralDataSetIterator result = new BufferedNeuralDataSetIterator();
		this.iterators.add(result);
		return result;
	}

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

	private void writeDoubleArray(final NeuralData data) {
		try {
			for (int i = 0; i < data.size(); i++) {
				this.output.writeDouble(data.getData(i));
			}
		} catch (final IOException e) {
			throw new NeuralDataError(e);
		}
	}

}
