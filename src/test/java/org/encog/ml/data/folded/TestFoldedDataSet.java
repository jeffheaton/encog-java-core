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
