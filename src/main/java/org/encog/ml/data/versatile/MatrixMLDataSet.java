package org.encog.ml.data.versatile;

import java.util.Iterator;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.util.EngineArray;

public class MatrixMLDataSet implements MLDataSet {
	
	/**
	 * An iterator to be used with the BasicMLDataSet. This iterator does not
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
			return this.currentIndex < MatrixMLDataSet.this.data.length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final MLDataPair next() {
			if (!hasNext()) {
				return null;
			}

			BasicMLData input = new BasicMLData(MatrixMLDataSet.this.calculatedInputSize);
			BasicMLData ideal = new BasicMLData(MatrixMLDataSet.this.calculatedIdealSize);
			double[] dataRow = MatrixMLDataSet.this.data[this.currentIndex];
			
			EngineArray.arrayCopy(dataRow, 0, input.getData(), 0, MatrixMLDataSet.this.calculatedInputSize);
			EngineArray.arrayCopy(dataRow, MatrixMLDataSet.this.calculatedInputSize, 
					ideal.getData(), 0, MatrixMLDataSet.this.calculatedIdealSize);
			
			MLDataPair pair = new BasicMLDataPair(input,ideal);
			this.currentIndex++;
			
			return pair;
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
	
	public MatrixMLDataSet() {
		
	}
	
	public MatrixMLDataSet(double[][] theData,int theCalculatedInputSize, int theCalculatedIdealSize) {
		this.data = theData;
		this.calculatedInputSize = theCalculatedInputSize;
		this.calculatedIdealSize = theCalculatedIdealSize;
	}

	@Override
	public Iterator<MLDataPair> iterator() {
		return new MatrixMLDataSetIterator();
	}

	@Override
	public int getIdealSize() {
		return this.calculatedIdealSize;
	}

	@Override
	public int getInputSize() {
		return this.calculatedInputSize;
	}

	@Override
	public boolean isSupervised() {
		return getIdealSize()==0;
	}

	@Override
	public long getRecordCount() {
		return this.data.length;
	}

	@Override
	public void getRecord(long index, MLDataPair pair) {
		double[] dataRow = MatrixMLDataSet.this.data[(int)index];
		
		EngineArray.arrayCopy(dataRow, 0, pair.getInputArray(), 0, MatrixMLDataSet.this.calculatedInputSize);
		EngineArray.arrayCopy(dataRow, MatrixMLDataSet.this.calculatedInputSize, 
				pair.getIdealArray(), 0, MatrixMLDataSet.this.calculatedIdealSize);
		

	}

	@Override
	public MLDataSet openAdditional() {
		return new MatrixMLDataSet(this.data,this.calculatedInputSize, this.calculatedIdealSize);
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MLDataPair get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the calculatedInputSize
	 */
	public int getCalculatedInputSize() {
		return calculatedInputSize;
	}

	/**
	 * @param calculatedInputSize the calculatedInputSize to set
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
	 * @param calculatedIdealSize the calculatedIdealSize to set
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
	 * @param data the data to set
	 */
	public void setData(double[][] data) {
		this.data = data;
	}
	
	

}
