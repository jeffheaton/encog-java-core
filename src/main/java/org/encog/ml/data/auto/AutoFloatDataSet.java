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
import org.encog.ml.data.basic.BasicMLDataSet;

public class AutoFloatDataSet implements Serializable, MLDataSet {
	
	private int sourceInputCount;
	private int sourceIdealCount;
	private int inputWindowSize;
	private int outputWindowSize;
	private List<AutoFloatColumn> columns = new ArrayList<AutoFloatColumn>();

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
	
	public AutoFloatDataSet(int theInputCount, int theIdealCount, int theInputWindowSize, int theOutputWindowSize) {
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
		return this.sourceIdealCount*this.outputWindowSize;
	}

	@Override
	public int getInputSize() {
		return this.sourceInputCount*this.inputWindowSize;
	}

	@Override
	public boolean isSupervised() {
		return getIdealSize()>0;
	}

	@Override
	public long getRecordCount() {
		if( this.columns.size()==0 ) {
			return 0;
		} else {
			return this.columns.get(0).getData().length;
		}
	}

	@Override
	public void getRecord(long index, MLDataPair pair) {
		int inputIndex = 0;
		for(int columnID=0;columnID<this.columns.size();columnID++) {
			AutoFloatColumn column = this.columns.get(columnID);
			
			// copy the input
			for(int i=0;i<this.inputWindowSize;i++) {
				pair.getInputArray()[inputIndex++] = column.getData()[(int)index+i];
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
		return (int)getRecordCount()-this.inputWindowSize-this.outputWindowSize;
	}

	@Override
	public MLDataPair get(int index) {
		MLDataPair result = BasicMLDataPair.createPair(getInputSize(), this.getIdealSize());
		getRecord(index,result);		
		return result;
	}

}
