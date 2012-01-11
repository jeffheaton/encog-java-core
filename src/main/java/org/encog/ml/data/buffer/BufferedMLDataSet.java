/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.data.buffer;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataError;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;

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
public class BufferedMLDataSet implements
	MLDataSet, Serializable {

	/**
	 * The version.
	 */
	private static final long serialVersionUID = 2577778772598513566L;

	/**
	 * Error message for ADD.
	 */
	public static final String ERROR_ADD 
		= "Add can only be used after calling beginLoad.";

	/**
	 * Error message for REMOVE.
	 */
	public static final String ERROR_REMOVE 
		= "Remove is not supported for BufferedNeuralDataSet.";

	/**
	 * True, if we are in the process of loading.
	 */
	private transient boolean loading;

	/**
	 * The file being used.
	 */
	private File file;

	/**
	 * The EGB file we are working wtih.
	 */
	private transient EncogEGBFile egb;

	/**
	 * Additional sets that were opened.
	 */
	private transient List<BufferedMLDataSet> additional 
		= new ArrayList<BufferedMLDataSet>();

	/**
	 * The owner.
	 */
	private transient BufferedMLDataSet owner;

	/**
	 * Construct the dataset using the specified binary file.
	 * 
	 * @param binaryFile
	 *            The file to use.
	 */
	public BufferedMLDataSet(final File binaryFile) {
		this.file = binaryFile;
		this.egb = new EncogEGBFile(binaryFile);
		if (file.exists()) {
			this.egb.open();
		}
	}

	/**
	 * Open the binary file for reading.
	 */
	public final void open() {
		this.egb.open();
	}

	/**
	 * @return An iterator.
	 */
	@Override
	public final Iterator<MLDataPair> iterator() {
		return new BufferedDataSetIterator(this);
	}

	/**
	 * @return The record count.
	 */
	@Override
	public final long getRecordCount() {
		if (this.egb == null) {
			return 0;
		} else {
			return this.egb.getNumberOfRecords();
		}
	}

	/**
	 * Read an individual record.
	 * 
	 * @param index
	 *            The zero-based index. Specify 0 for the first record, 1 for
	 *            the second, and so on.
	 * @param pair
	 *            THe data to read.
	 */
	@Override
	public final void getRecord(final long index, final MLDataPair pair) {
		this.egb.setLocation((int) index);
		double[] inputTarget = pair.getInputArray();
		this.egb.read(inputTarget);

		if (pair.getIdealArray() != null) {
			double[] idealTarget = pair.getIdealArray();
			this.egb.read(idealTarget);
		}
		
		this.egb.read();
	}

	/**
	 * @return An additional training set.
	 */
	@Override
	public final BufferedMLDataSet openAdditional() {

		BufferedMLDataSet result = new BufferedMLDataSet(this.file);
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
	public final void add(final MLData data1) {
		if (!this.loading) {
			throw new MLDataError(BufferedMLDataSet.ERROR_ADD);
		}

		egb.write(data1.getData());
		egb.write(1.0);
	}

	/**
	 * Add both the input and ideal data.
	 * 
	 * @param inputData
	 *            The input data.
	 * @param idealData
	 *            The ideal data.
	 */
	public final void add(final MLData inputData, final MLData idealData) {

		if (!this.loading) {
			throw new MLDataError(BufferedMLDataSet.ERROR_ADD);
		}

		this.egb.write(inputData.getData());
		this.egb.write(idealData.getData());
		this.egb.write((double)1.0);
	}

	/**
	 * Add a data pair of both input and ideal data.
	 * 
	 * @param pair
	 *            The pair to add.
	 */
	public final void add(final MLDataPair pair) {
		if (!this.loading) {
			throw new MLDataError(BufferedMLDataSet.ERROR_ADD);
		}

		this.egb.write(pair.getInputArray());
		this.egb.write(pair.getIdealArray());
		this.egb.write(pair.getSignificance());

	}

	/**
	 * Close the dataset.
	 */
	@Override
	public final void close() {

		Object[] obj = this.additional.toArray();

		for (int i = 0; i < obj.length; i++) {
			BufferedMLDataSet set = (BufferedMLDataSet) obj[i];
			set.close();
		}

		this.additional.clear();

		if (this.owner != null) {
			this.owner.removeAdditional(this);
		}

		this.egb.close();
		this.egb = null;
	}

	/**
	 * @return The ideal data size.
	 */
	@Override
	public final int getIdealSize() {
		if (this.egb == null) {
			return 0;
		} else {
			return this.egb.getIdealCount();
		}
	}

	/**
	 * @return The input data size.
	 */
	@Override
	public final int getInputSize() {
		if (this.egb == null) {
			return 0;
		} else {
			return this.egb.getInputCount();
		}
	}

	/**
	 * @return True if this dataset is supervised.
	 */
	@Override
	public final boolean isSupervised() {
		if (this.egb == null) {
			return false;
		} else {
			return this.egb.getIdealCount() > 0;
		}
	}

	/**
	 * @return If this dataset was created by openAdditional, the set that
	 *         created this object is the owner. Return the owner.
	 */
	public final BufferedMLDataSet getOwner() {
		return owner;
	}

	/**
	 * Set the owner of this dataset.
	 * 
	 * @param theOwner
	 *            The owner.
	 */
	public final void setOwner(final BufferedMLDataSet theOwner) {
		this.owner = theOwner;
	}

	/**
	 * Remove an additional dataset that was created.
	 * 
	 * @param child
	 *            The additional dataset to remove.
	 */
	public final void removeAdditional(final BufferedMLDataSet child) {
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
	public final void beginLoad(final int inputSize, final int idealSize) {
		this.egb.create(inputSize, idealSize);
		this.loading = true;
	}

	/**
	 * This method should be called once all the data has been loaded. The
	 * underlying file will be closed. The binary fill will then be opened for
	 * reading.
	 */
	public final void endLoad() {
		if (!this.loading) {
			throw new BufferedDataError("Must call beginLoad, before endLoad.");
		}

		this.egb.close();

		open();

	}

	/**
	 * @return The binary file used.
	 */
	public final File getFile() {
		return this.file;
	}

	/**
	 * @return The EGB file to use.
	 */
	public final EncogEGBFile getEGB() {
		return this.egb;
	}

	/**
	 * Load the binary dataset to memory. Memory access is faster.
	 * 
	 * @return A memory dataset.
	 */
	public final MLDataSet loadToMemory() {
		BasicMLDataSet result = new BasicMLDataSet();

		for (MLDataPair pair : this) {
			result.add(pair);
		}

		return result;
	}

	/**
	 * Load the specified training set.
	 * 
	 * @param training
	 *            The training set to load.
	 */
	public final void load(final MLDataSet training) {
		beginLoad(training.getInputSize(), training.getIdealSize());
		for (final MLDataPair pair : training) {
			add(pair);
		}
		endLoad();
	}
	

	@Override
	public int size() {
		return (int)getRecordCount();
	}

	@Override
	public MLDataPair get(int index) {
		MLDataPair result = BasicMLDataPair.createPair(getInputSize(), getIdealSize());
		this.getRecord(index, result);
		return result;
	}
}
