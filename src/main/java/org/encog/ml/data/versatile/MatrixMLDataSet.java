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
 * 
 * Lag 0; Lead 0 [10 rows] 1->1 2->2 3->3 4->4 5->5 6->6 7->7 8->8 9->9 10->10
 * 
 * Lag 0; Lead 1 [9 rows] 1->2 2->3 3->4 4->5 5->6 6->7 7->8 8->9 9->10
 * 
 * Lag 1; Lead 0 [9 rows, not useful] 1,2->1 2,3->2 3,4->3 4,5->4 5,6->5 6,7->6
 * 7,8->7 8,9->8 9,10->9
 * 
 * Lag 1; Lead 1 [8 rows] 1,2->3 2,3->4 3,4->5 4,5->6 5,6->7 6,7->8 7,8->9
 * 8,9->10
 * 
 * Lag 1; Lead 2 [7 rows] 1,2->3,4 2,3->4,5 3,4->5,6 4,5->6,7 5,6->7,8 6,7->8,9
 * 7,8->9,10
 * 
 * Lag 2; Lead 1 [7 rows] 1,2,3->4 2,3,4->5 3,4,5->6 4,5,6->7 5,6,7->8 6,7,8->9
 * 7,8,9->10
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

	private int calculatedInputSize = -1;
	private int calculatedIdealSize = -1;
	private double[][] data;
	private int[] mask;
	private int lagWindowSize = 0;
	private int leadWindowSize = 0;

	public MatrixMLDataSet() {

	}

	public MatrixMLDataSet(double[][] theData, int theCalculatedInputSize,
			int theCalculatedIdealSize) {
		this.data = theData;
		this.calculatedInputSize = theCalculatedInputSize;
		this.calculatedIdealSize = theCalculatedIdealSize;
	}

	public MatrixMLDataSet(double[][] theData, int inputCount, int idealCount,
			int[] theMask) {
		this.data = theData;
		this.calculatedInputSize = inputCount;
		this.calculatedIdealSize = idealCount;
		this.mask = theMask;
	}

	public MatrixMLDataSet(MatrixMLDataSet data, int[] mask) {
		this.data = data.getData();
		this.calculatedInputSize = data.getCalculatedInputSize();
		this.calculatedIdealSize = data.getCalculatedIdealSize();
		this.mask = mask;
	}

	public int[] getMask() {
		return this.mask;
	}

	@Override
	public Iterator<MLDataPair> iterator() {
		return new MatrixMLDataSetIterator();
	}

	@Override
	public int getIdealSize() {
		return this.calculatedIdealSize * Math.min(this.leadWindowSize, 1);
	}

	@Override
	public int getInputSize() {
		return this.calculatedInputSize * this.lagWindowSize;
	}

	@Override
	public boolean isSupervised() {
		return getIdealSize() == 0;
	}

	@Override
	public long getRecordCount() {
		if( this.data==null ) {
			throw new EncogError("You must normalize the dataset before using it.");
		}
		
		if (this.mask == null) {
			return this.data.length
					- (this.lagWindowSize + this.leadWindowSize);
		}
		return this.mask.length - (this.lagWindowSize + this.leadWindowSize);
	}
	
	private int calculateLagCount() {
		return (MatrixMLDataSet.this.lagWindowSize <= 0) ? 1: (this.lagWindowSize+1);
	}
	
	private int calculateLeadCount() {
		return (this.leadWindowSize <= 1) ? 1
				: this.leadWindowSize;
	}

	@Override
	public void getRecord(long index, MLDataPair pair) {
		if( this.data==null ) {
			throw new EncogError("You must normalize the dataset before using it.");
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
			double[] dataRow = lookupDataRow((int) (index + i+outputStart));
			EngineArray.arrayCopy(dataRow, this.calculatedInputSize, pair.getIdealArray(), i
					* MatrixMLDataSet.this.calculatedIdealSize,
					MatrixMLDataSet.this.calculatedIdealSize);
		}
	}

	private double[] lookupDataRow(int index) {
		if (this.mask != null) {
			return this.data[this.mask[index]];
		} else {
			return this.data[index];
		}
	}

	@Override
	public MLDataSet openAdditional() {
		MatrixMLDataSet result = new MatrixMLDataSet(this.data,
				this.calculatedInputSize, this.calculatedIdealSize, this.mask);
		result.setLagWindowSize(getLagWindowSize());
		result.setLeadWindowSize(getLeadWindowSize());
		return result;
	}

	@Override
	public void add(MLData data1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(MLData inputData, MLData idealData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(MLDataPair inputData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		return (int) getRecordCount();
	}

	@Override
	public MLDataPair get(int index) {
		if (index>size()) {
			return null;
		}

		BasicMLData input = new BasicMLData(
				MatrixMLDataSet.this.calculatedInputSize*calculateLagCount());
		BasicMLData ideal = new BasicMLData(
				MatrixMLDataSet.this.calculatedIdealSize*calculateLeadCount());
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
