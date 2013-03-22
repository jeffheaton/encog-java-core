package org.encog.ml.data.folded;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.encog.Encog;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.junit.Test;

public class TestFoldedDataSet extends TestCase {
	
	private FoldedDataSet performFoldTest(int recordCount, int foldCount, int testIndex1, int testCount1, int testIndex2, int testCount2) {
		MLDataSet dataSet = new BasicMLDataSet();
		
		for(int i=0;i<recordCount;i++) {
			final MLDataPair pair = BasicMLDataPair.createPair(4,1);
			pair.getIdeal().setData(0, i);
			dataSet.add(pair);
		}
		
		FoldedDataSet foldedData = new FoldedDataSet(dataSet);
		foldedData.fold(foldCount);
		
		foldedData.setCurrentFold(testIndex1);
		Assert.assertEquals(testCount1, foldedData.size());
		
		foldedData.setCurrentFold(testIndex2);
		Assert.assertEquals(testCount2, foldedData.size());
		
		testFoldedAccess(dataSet, foldedData);
		
		return foldedData;
	}
	
	private void testFoldedAccess(MLDataSet dataSet, FoldedDataSet foldedDataSet) {
		int parentIndex = 0;

		for(int i=0;i<foldedDataSet.getNumFolds();i++) {
			foldedDataSet.setCurrentFold(i);
			int expectedCount = foldedDataSet.size();
			int actualCount = 0;
			for(MLDataPair pair: foldedDataSet) {
				actualCount++;
				Assert.assertEquals(parentIndex, pair.getIdeal().getData(0), Encog.DEFAULT_DOUBLE_EQUAL);
				parentIndex++;
			}
			Assert.assertEquals(expectedCount, actualCount);
		}
	}
	
	@Test
	public void testFoldedEven() {
		performFoldTest(50, 5, 0, 10, 4, 10);
	}
	
	@Test
	public void testFoldedOdd() {
		performFoldTest(53, 5, 0, 10, 4, 13);
	}
	
	@Test
	public void testFoldedSmall() {
		performFoldTest(3, 5, 0, 1, 2, 1);
	}
}
