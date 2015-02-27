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
package org.encog.ml.data.versatile;

import java.util.Iterator;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.util.EngineArray;

/**
 * The MatrixMLDataSet can use a large 2D matrix of doubles to internally hold
 * data. It supports several advanced features such as the ability to mask and
 * time-box. Masking allows several datasets to use the same backing array,
 * however use different parts.
 * 
 * Time boxing allows time-series data to be represented for prediction. The
 * following shows how data is laid out for different lag and lead settings.
 * 
 * Lag 0; Lead 0 [10 rows] 1→1 2→2 3→3 4→4 5→5 6→6 7→7 8→8 9→9 10→10
 * 
 * Lag 0; Lead 1 [9 rows] 1→2 2→3 3→4 4→5 5→6 6→7 7→8 8→9 9→10
 * 
 * Lag 1; Lead 0 [9 rows, not useful] 1,2→1 2,3→2 3,4→3 4,5→4 5,6→5 6,7→6
 * 7,8→7 8,9→8 9,10→9
 * 
 * Lag 1; Lead 1 [8 rows] 1,2→3 2,3→4 3,4→5 4,5→6 5,6→7 6,7→8 7,8→9
 * 8,9→10
 * 
 * Lag 1; Lead 2 [7 rows] 1,2→3,4 2,3→4,5 3,4→5,6 4,5→6,7 5,6→7,8 6,7→8,9
 * 7,8→9,10
 * 
 * Lag 2; Lead 1 [7 rows] 1,2,3→4 2,3,4→5 3,4,5→6 4,5,6→7 5,6,7→8 6,7,8→9
 * 7,8,9→10
 */
public class MatrixMLDataSet implements MLDataSet {

	/**
	 * An iterator to be used with the MatrixMLDataSet. This iterator does not
	 * support removes.
	 * 
	 * @author jheaton
	 */
	public class MatrixMLDataSetIterator implements Iterator<MLDataPair> {

		/**
		 * The index that the iterator is currently at.
		 */
		private int currentIndex = 0;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean hasNext() {
			return this.currentIndex < MatrixMLDataSet.this.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final MLDataPair next() {
			if (!hasNext()) {
				return null;
			}

			return MatrixMLDataSet.this.get(this.currentIndex++);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void remove() {
			throw new EncogError("Called remove, unsupported operation.");
		}
	}

	/**
	 * The number of inputs.
	 */
	private int calculatedInputSize = -1;
	
	/**
	 * The number of ideal values.
	 */
	private int calculatedIdealSize = -1;
	
	/**
	 * The backing data.
	 */
	private double[][] data;
	
	/**
	 * The mask to the data.
	 */
	private int[] mask;
	
	/**
	 * The lag window size.
	 */
	private int lagWindowSize = 0;
	
	/**
	 * The lead window size.
	 */
	private int leadWindowSize = 0;

	/**
	 * The default constructor.
	 */
	public MatrixMLDataSet() {

	}

	/**
	 * Construct the dataset with no mask.
	 * @param theData The backing array.
	 * @param theCalculatedInputSize The input size.
	 * @param theCalculatedIdealSize The ideal size.
	 */
	public MatrixMLDataSet(double[][] theData, int theCalculatedInputSize,
			int theCalculatedIdealSize) {
		this.data = theData;
		this.calculatedInputSize = theCalculatedInputSize;
		this.calculatedIdealSize = theCalculatedIdealSize;
	}

	/**
	 * Construct the dataset from a 2D double array..
	 * @param theData The data.
	 * @param inputCount The input count.
	 * @param idealCount The ideal count.
	 * @param theMask The mask.
	 */
	public MatrixMLDataSet(double[][] theData, int inputCount, int idealCount,
			int[] theMask) {
		this.data = theData;
		this.calculatedInputSize = inputCount;
		this.calculatedIdealSize = idealCount;
		this.mask = theMask;
	}

	/**
	 * Construct the dataset from another matrix dataset.
	 * @param data The data.
	 * @param mask The mask.
	 */
	public MatrixMLDataSet(MatrixMLDataSet data, int[] mask) {
		this.data = data.getData();
		this.calculatedInputSize = data.getCalculatedInputSize();
		this.calculatedIdealSize = data.getCalculatedIdealSize();
		this.mask = mask;
	}

	/**
	 * @return The mask.
	 */
	public int[] getMask() {
		return this.mask;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<MLDataPair> iterator() {
		return new MatrixMLDataSetIterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIdealSize() {
		return this.calculatedIdealSize * Math.min(this.leadWindowSize, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputSize() {
		return this.calculatedInputSize * this.lagWindowSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSupervised() {
		return getIdealSize() == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getRecordCount() {
		if (this.data == null) {
			throw new EncogError(
					"You must normalize the dataset before using it.");
		}

		if (this.mask == null) {
			return this.data.length
					- (this.lagWindowSize + this.leadWindowSize);
		}
		return this.mask.length - (this.lagWindowSize + this.leadWindowSize);
	}

	private int calculateLagCount() {
		return (MatrixMLDataSet.this.lagWindowSize <= 0) ? 1
				: (this.lagWindowSize + 1);
	}

	private int calculateLeadCount() {
		return (this.leadWindowSize <= 1) ? 1 : this.leadWindowSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getRecord(long index, MLDataPair pair) {
		if (this.data == null) {
			throw new EncogError(
					"You must normalize the dataset before using it.");
		}

		// Copy the input, account for time windows.
		int inputSize = calculateLagCount();
		for (int i = 0; i < inputSize; i++) {
			double[] dataRow = lookupDataRow((int) (index + i));

			EngineArray.arrayCopy(dataRow, 0, pair.getInput().getData(), i
					* MatrixMLDataSet.this.calculatedInputSize,
					MatrixMLDataSet.this.calculatedInputSize);
		}

		// Copy the output, account for time windows.
		int outputStart = (this.leadWindowSize > 0) ? 1 : 0;
		int outputSize = calculateLeadCount();
		for (int i = 0; i < outputSize; i++) {
			double[] dataRow = lookupDataRow((int) (index + i + outputStart));
			EngineArray.arrayCopy(dataRow, this.calculatedInputSize,
					pair.getIdealArray(), i
							* MatrixMLDataSet.this.calculatedIdealSize,
					MatrixMLDataSet.this.calculatedIdealSize);
		}
	}

	/**
	 * Find a row, using the mask.
	 * @param index The index we seek.
	 * @return The row.
	 */
	private double[] lookupDataRow(int index) {
		if (this.mask != null) {
			return this.data[this.mask[index]];
		} else {
			return this.data[index];
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLDataSet openAdditional() {
		MatrixMLDataSet result = new MatrixMLDataSet(this.data,
				this.calculatedInputSize, this.calculatedIdealSize, this.mask);
		result.setLagWindowSize(getLagWindowSize());
		result.setLeadWindowSize(getLeadWindowSize());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(MLData data1) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(MLData inputData, MLData idealData) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(MLDataPair inputData) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return (int) getRecordCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MLDataPair get(int index) {
		if (index > size()) {
			return null;
		}

		BasicMLData input = new BasicMLData(
				MatrixMLDataSet.this.calculatedInputSize * calculateLagCount());
		BasicMLData ideal = new BasicMLData(
				MatrixMLDataSet.this.calculatedIdealSize * calculateLeadCount());
		MLDataPair pair = new BasicMLDataPair(input, ideal);

		MatrixMLDataSet.this.getRecord(index, pair);

		return pair;
	}

	/**
	 * @return the calculatedInputSize
	 */
	public int getCalculatedInputSize() {
		return calculatedInputSize;
	}

	/**
	 * @param calculatedInputSize
	 *            the calculatedInputSize to set
	 */
	public void setCalculatedInputSize(int calculatedInputSize) {
		this.calculatedInputSize = calculatedInputSize;
	}

	/**
	 * @return the calculatedIdealSize
	 */
	public int getCalculatedIdealSize() {
		return calculatedIdealSize;
	}

	/**
	 * @param calculatedIdealSize
	 *            the calculatedIdealSize to set
	 */
	public void setCalculatedIdealSize(int calculatedIdealSize) {
		this.calculatedIdealSize = calculatedIdealSize;
	}

	/**
	 * @return the data
	 */
	public double[][] getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(double[][] data) {
		this.data = data;
	}

	/**
	 * @return the lagWindowSize
	 */
	public int getLagWindowSize() {
		return lagWindowSize;
	}

	/**
	 * @param lagWindowSize
	 *            the lagWindowSize to set
	 */
	public void setLagWindowSize(int lagWindowSize) {
		this.lagWindowSize = lagWindowSize;
	}

	/**
	 * @return the leadWindowSize
	 */
	public int getLeadWindowSize() {
		return leadWindowSize;
	}

	/**
	 * @param leadWindowSize
	 *            the leadWindowSize to set
	 */
	public void setLeadWindowSize(int leadWindowSize) {
		this.leadWindowSize = leadWindowSize;
	}

}
