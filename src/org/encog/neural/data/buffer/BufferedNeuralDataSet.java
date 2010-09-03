/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */
package org.encog.neural.data.buffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.encog.engine.data.EngineData;
import org.encog.neural.data.Indexable;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataError;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.persist.EncogCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.BufferedNeuralDataSetPersistor;

/**
 * This class is not memory based, so very long files can be used, without
 * running out of memory. This dataset uses a Encog binary training file as a
 * buffer.
 * 
 * When used with a slower access dataset, such as CSV, XML or SQL, where
 * parsing must occur, this dataset can be used to load from the slower dataset
 * and train at much higher speeds.
 * 
 * This class makes use of Java file channels for maximum file access
 * performance.
 * 
 * If you are going to create a binary file, by using the add methods, you must
 * call beginLoad to cause Encog to open an output file. Once the data has been
 * loaded, call endLoad. You can also use the BinaryDataLoader class, with a
 * CODEC, to load many other popular external formats.
 * 
 * The binary files produced by this class are in the Encog binary training
 * format, and can be used with any Encog platform. Encog binary files are
 * stored using "little endian" numbers.
 */
public class BufferedNeuralDataSet implements NeuralDataSet, Indexable, EncogPersistedObject {

	/**
	 * The version.
	 */
	private static final long serialVersionUID = 2577778772598513566L;

	/**
	 * The size of a double.
	 */
	public final static int DOUBLE_SIZE = Double.SIZE / 8;
	
	public final static int HEADER_SIZE = DOUBLE_SIZE*3;

	/**
	 * Error message for ADD.
	 */
	public static final String ERROR_ADD = "Add can only be used after calling beginLoad.";

	/**
	 * Error message for REMOVE.
	 */
	public static final String ERROR_REMOVE = "Remove is not supported for BufferedNeuralDataSet.";

	/**
	 * The record count.
	 */
	private int recordCount;

	/**
	 * The size of input data.
	 */
	private int inputSize;

	/**
	 * The size of ideal data.
	 */
	private int idealSize;

	/**
	 * The size of a record.
	 */
	private int recordSize;

	/**
	 * The file being used.
	 */
	private File file;

	/**
	 * The input stream.
	 */
	private FileInputStream stream;

	/**
	 * The output stream.
	 */
	private RandomAccessFile output;

	/**
	 * The file channel.
	 */
	private FileChannel fileChannel;

	/**
	 * The byte buffer.
	 */
	private ByteBuffer byteBuffer;

	/**
	 * Additional sets that were opened.
	 */
	private List<BufferedNeuralDataSet> additional = new ArrayList<BufferedNeuralDataSet>();

	/**
	 * The owner;
	 */
	private BufferedNeuralDataSet owner;

	/**
	 * The Encog persisted object name.
	 */
	private String name;
	
	/**
	 * The Encog persisted object description.
	 */
	private String description;
	
	/**
	 * The Encog persisted object collection.
	 */
	private EncogCollection collection;
	
	/**
	 * The current write position.
	 */
	private int currentWritePosition;
	
	/**
	 * Construct the dataset using the specified binary file.
	 * 
	 * @param binaryFile
	 *            The file to use.
	 */
	public BufferedNeuralDataSet(File binaryFile) {
		this.file = binaryFile;
		open();
	}

	/**
	 * Open the binary file for reading.
	 */
	public void open() {
		try {
			if( this.stream!=null || this.fileChannel!=null )
				throw new BufferedDataError("Dataset is already open.");
				
			this.stream = new FileInputStream(this.file);
			this.fileChannel = this.stream.getChannel();
			this.byteBuffer = this.fileChannel.map(MapMode.READ_ONLY, 0,
					BufferedNeuralDataSet.DOUBLE_SIZE * 3);
			this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			this.currentWritePosition = 0;
			
			boolean isEncogFile = true;

			isEncogFile = isEncogFile ? this.byteBuffer.get(0) == 'E' : false;
			isEncogFile = isEncogFile ? this.byteBuffer.get(1) == 'N' : false;
			isEncogFile = isEncogFile ? this.byteBuffer.get(2) == 'C' : false;
			isEncogFile = isEncogFile ? this.byteBuffer.get(3) == 'O' : false;
			isEncogFile = isEncogFile ? this.byteBuffer.get(4) == 'G' : false;
			isEncogFile = isEncogFile ? this.byteBuffer.get(5) == '-' : false;

			if (!isEncogFile)
				throw new BufferedDataError(
						"File is not a valid Encog binary file.");

			char v1 = (char) this.byteBuffer.get(6);
			char v2 = (char) this.byteBuffer.get(7);
			String versionStr = "" + v1 + v2;

			try {
				int version = Integer.parseInt(versionStr);
				if (version > 0)
					throw new BufferedDataError(
							"File is from a newer version of Encog than is currently in use.");
			} catch (NumberFormatException ex) {
				throw new BufferedDataError("File has invalid version number.");
			}

			DoubleBuffer db = this.byteBuffer.asDoubleBuffer();

			this.inputSize = (int) db.get(1);
			this.idealSize = (int) db.get(2);

			this.recordSize = (inputSize + idealSize)
					* BufferedNeuralDataSet.DOUBLE_SIZE;

			this.recordCount = (int) ((this.file.length() - (BufferedNeuralDataSet.DOUBLE_SIZE * 3)) / this.recordSize);

			this.byteBuffer = this.fileChannel.map(MapMode.READ_ONLY,
					3 * BufferedNeuralDataSet.DOUBLE_SIZE, recordCount
							* recordSize);
		} catch(FileNotFoundException ex) {
			// can't find the file, we are probably getting ready to create it.
			this.byteBuffer = null;
			this.stream = null;
			this.fileChannel = null;
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}

	/**
	 * @return An iterator.
	 */
	@Override
	public Iterator<NeuralDataPair> iterator() {
		return new BufferedDataSetIterator(this);
	}


	/**
	 * @return The record count.
	 */
	@Override
	public long getRecordCount() {
		return this.recordCount;
	}

	/**
	 * Read an individual record.
	 * 
	 * @param The
	 *            zero-based index. Specify 0 for the first record, 1 for the
	 *            second, and so on.
	 */
	@Override
	public void getRecord(long index, EngineData pair) {
		double[] inputTarget = pair.getInputArray();
		double[] idealTarget = pair.getIdealArray();

		this.byteBuffer.position((int) (this.recordSize * index));
		for (int i = 0; i < this.inputSize; i++) {
			inputTarget[i] = this.byteBuffer.getDouble();
		}

		for (int i = 0; i < this.idealSize; i++) {
			idealTarget[i] = this.byteBuffer.getDouble();
		}
	}

	/**
	 * @return An additional training set.
	 */
	@Override
	public BufferedNeuralDataSet openAdditional() {

		BufferedNeuralDataSet result = new BufferedNeuralDataSet(this.file);
		result.setOwner(this);
		this.additional.add(result);
		return result;
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
		writeDoubleArray(this.byteBuffer, data1);

	}

	/**
	 * Write a double array from the specified data to the file.
	 * 
	 * @param buffer
	 *            The buffer to write to.
	 * @param data
	 *            The data that holds the array.
	 */
	private void writeDoubleArray(ByteBuffer buffer, final NeuralData data) {
		for (int i = 0; i < data.size(); i++) {
			buffer.putDouble(data.getData(i));
		}
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
		try {
		if (this.output == null) {
			throw new NeuralDataError(BufferedNeuralDataSet.ERROR_ADD);
		}
		this.byteBuffer = this.fileChannel.map(MapMode.READ_WRITE, this.currentWritePosition, this.recordSize);
		writeDoubleArray(this.byteBuffer, inputData);
		writeDoubleArray(this.byteBuffer, idealData);
		this.currentWritePosition+=this.recordSize;
		}
		catch(IOException ex)
		{
			throw new BufferedDataError(ex);
		}
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
		writeDoubleArray(this.byteBuffer, inputData.getInput());
		if (inputData.getIdeal() != null) {
			writeDoubleArray(this.byteBuffer, inputData.getIdeal());
		}
	}

	/**
	 * Close the dataset.
	 */
	@Override
	public void close() {
		try {
			Object[] obj = this.additional.toArray();

			for (int i = 0; i < obj.length; i++) {
				BufferedNeuralDataSet set = (BufferedNeuralDataSet) obj[i];
				set.close();
			}

			this.additional.clear();

			if (this.owner != null) {
				this.owner.removeAdditional(this);
			}

			if (this.output != null)
				endLoad();

			if (this.fileChannel != null) {
				this.fileChannel.close();
				this.fileChannel = null;
			}	

			if (this.stream != null) {
				this.stream.close();
				this.stream = null;
			}
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}

	/**
	 * @return The ideal data size.
	 */
	@Override
	public int getIdealSize() {
		return this.idealSize;
	}

	/**
	 * @return The input data size.
	 */
	@Override
	public int getInputSize() {
		return this.inputSize;
	}

	/**
	 * @return True if this dataset is supervised.
	 */
	@Override
	public boolean isSupervised() {
		return this.idealSize > 0;
	}

	/**
	 * @return If this dataset was created by openAdditional, the set that
	 *         created this object is the owner. Return the owner.
	 */
	public BufferedNeuralDataSet getOwner() {
		return owner;
	}

	/**
	 * Set the owner of this dataset.
	 * 
	 * @param owner
	 *            The owner.
	 */
	public void setOwner(BufferedNeuralDataSet owner) {
		this.owner = owner;
	}

	/**
	 * Remove an additional dataset that was created.
	 * 
	 * @param child
	 *            The additional dataset to remove.
	 */
	public void removeAdditional(BufferedNeuralDataSet child) {
		synchronized (this) {
			this.additional.remove(child);
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
			if (this.output != null)
				throw new BufferedDataError("File is already open for writing.");

			if (this.fileChannel != null) {
				this.fileChannel.close();
				this.fileChannel = null;
			}

			if (this.stream != null) {
				this.stream.close();
				this.stream = null;
			}

			this.inputSize = inputSize;
			this.idealSize = idealSize;
			this.recordSize = (getInputSize() * DOUBLE_SIZE)
					+ (getIdealSize() * DOUBLE_SIZE);
			this.file.delete();

			this.output = new RandomAccessFile(this.file, "rw");
			this.fileChannel = this.output.getChannel();
			this.byteBuffer = this.fileChannel.map(MapMode.READ_WRITE, 0,
					BufferedNeuralDataSet.DOUBLE_SIZE * 3);
			this.byteBuffer.put((byte) 'E');
			this.byteBuffer.put((byte) 'N');
			this.byteBuffer.put((byte) 'C');
			this.byteBuffer.put((byte) 'O');
			this.byteBuffer.put((byte) 'G');
			this.byteBuffer.put((byte) '-');
			this.byteBuffer.put((byte) '0');
			this.byteBuffer.put((byte) '0');
			this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			this.byteBuffer.putDouble(getInputSize());
			this.byteBuffer.putDouble(getIdealSize());
			this.currentWritePosition = this.byteBuffer.position();
		} catch (final IOException e) {
			throw new NeuralDataError(e);
		}
	}

	/**
	 * This method should be called once all the data has been loaded. The
	 * underlying file will be closed. The binary fill will then be opened for
	 * reading.
	 */
	public void endLoad() {
		try {
			if (this.output == null)
				throw new BufferedDataError(
						"Must call beginLoad, before endLoad.");

			this.output.close();
			this.output = null;
			
			this.fileChannel.close();
			this.fileChannel = null;

			open();
		} catch (final IOException e) {
			throw new NeuralDataError(e);
		}
	}

	/**
	 * @return An Encog persistor for this object.
	 */
	@Override
	public Persistor createPersistor() {
		return new BufferedNeuralDataSetPersistor();
	}

	/**
	 * @return The name of this object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this object, used for Encog persistance.
	 * @param name The name of this object.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The description of this object.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of this object.
	 * @param The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return The Encog persisted collection that this object belongs to.
	 */
	public EncogCollection getCollection() {
		return collection;
	}

	/**
	 * Set the Encog persisted collection that this object belongs to.
	 * @param collection The collection.
	 */
	public void setCollection(EncogCollection collection) {
		this.collection = collection;
	}

	/**
	 * @return The binary file used.
	 */
	public File getFile() {
		return this.file;
	}
}
