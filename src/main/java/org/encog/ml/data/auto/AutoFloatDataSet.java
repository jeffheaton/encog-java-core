/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core

 * Copyright 2008-2014 Heaton Research, Inc.
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
package org.encog.ml.data.auto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class AutoFloatDataSet implements Serializable, MLDataSet {

	private int sourceInputCount;
	private int sourceIdealCount;
	private int inputWindowSize;
	private int outputWindowSize;
	private List<AutoFloatColumn> columns = new ArrayList<AutoFloatColumn>();
	private float normalizedMax = 1;
	private float normalizedMin = -1;
	private boolean normalizationEnabled = false;

	public class AutoFloatIterator implements Iterator<MLDataPair> {

		/**
		 * The index that the iterator is currently at.
		 */
		private int currentIndex = 0;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean hasNext() {
			return this.currentIndex < AutoFloatDataSet.this.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final MLDataPair next() {
			if (!hasNext()) {
				return null;
			}

			return AutoFloatDataSet.this.get(this.currentIndex++);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void remove() {
			throw new EncogError("Called remove, unsupported operation.");
		}

	}

	public AutoFloatDataSet(int theInputCount, int theIdealCount,
			int theInputWindowSize, int theOutputWindowSize) {
		this.sourceInputCount = theInputCount;
		this.sourceIdealCount = theIdealCount;
		this.inputWindowSize = theInputWindowSize;
		this.outputWindowSize = theOutputWindowSize;
	}

	@Override
	public Iterator<MLDataPair> iterator() {
		return new AutoFloatIterator();
	}

	@Override
	public int getIdealSize() {
		return this.sourceIdealCount * this.outputWindowSize;
	}

	@Override
	public int getInputSize() {
		return this.sourceInputCount * this.inputWindowSize;
	}

	@Override
	public boolean isSupervised() {
		return getIdealSize() > 0;
	}

	@Override
	public long getRecordCount() {
		if (this.columns.size() == 0) {
			return 0;
		} else {
			int totalRows = this.columns.get(0).getData().length;
			int windowSize = this.inputWindowSize + this.outputWindowSize;
			return (totalRows - windowSize) + 1;
		}
	}

	@Override
	public void getRecord(long index, MLDataPair pair) {

		int columnID = 0;

		// copy the input
		int inputIndex = 0;
		for (int i = 0; i < this.sourceInputCount; i++) {
			AutoFloatColumn column = this.columns.get(columnID++);
			for (int j = 0; j < this.inputWindowSize; j++) {
				if( this.normalizationEnabled ) {
					pair.getInputArray()[inputIndex++] = column.getNormalized((int) index
						+ j, this.normalizedMin, this.normalizedMax);
				} else {
					pair.getInputArray()[inputIndex++] = column.getData()[(int) index
							+ j];
				}
			}
		}

		// copy the output
		int idealIndex = 0;
		for (int i = 0; i < this.sourceIdealCount; i++) {
			AutoFloatColumn column = this.columns.get(columnID++);
			for (int j = 0; j < this.outputWindowSize; j++) {
				if( this.normalizationEnabled ) {
					pair.getIdealArray()[idealIndex++] = column.getNormalized(
							(int) (this.inputWindowSize + index + j), this.normalizedMin, this.normalizedMax);
				} else {
					pair.getIdealArray()[idealIndex++] = column.getData()[
							(int) (this.inputWindowSize + index + j)];
				}
			}
		}

	}

	@Override
	public MLDataSet openAdditional() {
		return this;
	}

	@Override
	public void add(MLData data1) {
		throw new EncogError("Add's not supported by this dataset.");

	}

	@Override
	public void add(MLData inputData, MLData idealData) {
		throw new EncogError("Add's not supported by this dataset.");

	}

	@Override
	public void add(MLDataPair inputData) {
		throw new EncogError("Add's not supported by this dataset.");

	}

	@Override
	public void close() {

	}

	@Override
	public int size() {
		return (int)getRecordCount();
	}

	@Override
	public MLDataPair get(int index) {
		if( index>=size() ) {
			return null;
		}

		MLDataPair result = BasicMLDataPair.createPair(getInputSize(),
				this.getIdealSize());
		getRecord(index, result);
		return result;
	}

	public void addColumn(float[] data) {
		AutoFloatColumn column = new AutoFloatColumn(data);
		this.columns.add(column);

	}

	public void loadCSV(String filename, boolean headers, CSVFormat format, int[] input, int[] ideal) {
		// first, just size it up
		ReadCSV csv = new ReadCSV(filename,headers,format);
		int lineCount = 0;
		while(csv.next()) {
			lineCount++;
		}
		csv.close();

		// allocate space to hold it
		float[][] data = new float[input.length+ideal.length][lineCount];

		// now read the data in
		csv = new ReadCSV(filename,headers,format);
		int rowIndex = 0;
		while(csv.next()) {
			int columnIndex = 0;

			for(int i=0;i<input.length;i++) {
				data[columnIndex++][rowIndex] = (float)csv.getDouble(input[i]);
			}
			for(int i=0;i<ideal.length;i++) {
				data[columnIndex++][rowIndex] = (float)csv.getDouble(ideal[i]);
			}

			rowIndex++;
		}
		csv.close();

		// now add the columns
		for(int i=0;i<data.length;i++) {
			addColumn(data[i]);
		}
	}

	/**
	 * @return the normalizedMax
	 */
	public float getNormalizedMax() {
		return normalizedMax;
	}

	/**
	 * @param normalizedMax the normalizedMax to set
	 */
	public void setNormalizedMax(float normalizedMax) {
		this.normalizedMax = normalizedMax;
		this.normalizationEnabled = true;
	}

	/**
	 * @return the normalizedMin
	 */
	public float getNormalizedMin() {
		return normalizedMin;
	}

	/**
	 * @param normalizedMin the normalizedMin to set
	 */
	public void setNormalizedMin(float normalizedMin) {
		this.normalizedMin = normalizedMin;
		this.normalizationEnabled = true;
	}

	/**
	 * @return the normalizationEnabled
	 */
	public boolean isNormalizationEnabled() {
		return normalizationEnabled;
	}

	/**
	 * @param normalizationEnabled the normalizationEnabled to set
	 */
	public void setNormalizationEnabled(boolean normalizationEnabled) {
		this.normalizationEnabled = normalizationEnabled;
	}




}
