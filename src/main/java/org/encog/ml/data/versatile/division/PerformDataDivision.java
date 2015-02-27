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
package org.encog.ml.data.versatile.division;

import java.util.List;

import org.encog.mathutil.randomize.generate.GenerateRandom;
import org.encog.ml.data.versatile.MatrixMLDataSet;
import org.encog.ml.data.versatile.VersatileMLDataSet;

/**
 * Perform a data division.
 */
public class PerformDataDivision {
	/**
	 * True, if we should shuffle during division.
	 */
	private final boolean shuffle;
	
	/**
	 * A random number generator.
	 */
	private final GenerateRandom rnd;

	/**
	 * Construct the data division processor.
	 * @param theShuffle Should we shuffle?
	 * @param theRandom Random number generator, often seeded to be consistent. 
	 */
	public PerformDataDivision(boolean theShuffle, GenerateRandom theRandom) {
		this.shuffle = theShuffle;
		this.rnd = theRandom;
	}

	/**
	 * Perform the split. 
	 * @param dataDivisionList The list of data divisions.
	 * @param dataset The dataset to split.
	 * @param inputCount The input count.
	 * @param idealCount The ideal count.
	 */
	public void perform(List<DataDivision> dataDivisionList, VersatileMLDataSet dataset,
			int inputCount, int idealCount) {
		generateCounts(dataDivisionList, dataset.getData().length);
		generateMasks(dataDivisionList);
		if (this.shuffle) {
			performShuffle(dataDivisionList, dataset.getData().length);
		}
		createDividedDatasets(dataDivisionList, dataset, inputCount, idealCount);

	}

	/**
	 * Create the datasets that we will divide into.
	 * @param dataDivisionList The list of divisions.
	 * @param parentDataset The data set to divide.
	 * @param inputCount The input count.
	 * @param idealCount The ideal count.
	 */
	private void createDividedDatasets(List<DataDivision> dataDivisionList,
			VersatileMLDataSet parentDataset, int inputCount, int idealCount) {
		for (DataDivision division : dataDivisionList) {
			MatrixMLDataSet dataset = new MatrixMLDataSet(parentDataset.getData(), inputCount,
					idealCount, division.getMask());
			dataset.setLagWindowSize(parentDataset.getLagWindowSize());
			dataset.setLeadWindowSize(parentDataset.getLeadWindowSize());
			division.setDataset(dataset);
		}
	}

	/**
	 * Perform a Fisher-Yates shuffle.
	 * http://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
	 * 
	 * @param dataDivisionList
	 *            The division list.
	 */
	private void performShuffle(List<DataDivision> dataDivisionList,
			int totalCount) {
		for (int i = totalCount - 1; i > 0; i--) {
			int n = this.rnd.nextInt(i + 1);
			virtualSwap(dataDivisionList, i, n);
		}
	}

	/**
	 * Swap two items, across all divisions.
	 * @param dataDivisionList The division list
	 * @param a The index of the first item to swap.
	 * @param b The index of the second item to swap.
	 */
	private void virtualSwap(List<DataDivision> dataDivisionList, int a, int b) {
		DataDivision divA = null;
		DataDivision divB = null;
		int offsetA = 0;
		int offsetB = 0;
		
		// Find points a and b in the collections.
		int baseIndex = 0;
		for(DataDivision division: dataDivisionList) {
			baseIndex+=division.getCount();
			
			if( divA==null && a<baseIndex ) {
				divA = division;
				offsetA = a - (baseIndex - division.getCount());
			}
			if( divB==null && b<baseIndex ) {
				divB = division;
				offsetB = b - (baseIndex - division.getCount());
			}
		}
		
		// Swap a and b.
		int temp = divA.getMask()[offsetA];
		divA.getMask()[offsetA] = divB.getMask()[offsetB];
		divB.getMask()[offsetB] = temp;
	}

	/**
	 * Generate the masks, for all divisions.
	 * @param dataDivisionList The divisions.
	 */
	private void generateMasks(List<DataDivision> dataDivisionList) {
		int idx = 0;
		for (DataDivision division : dataDivisionList) {
			division.allocateMask(division.getCount());
			for (int i = 0; i < division.getCount(); i++) {
				division.getMask()[i] = idx++;
			}
		}
	}

	/**
	 * Generate the counts for all divisions, give remaining items to final division.
	 * @param dataDivisionList The division list.
	 * @param totalCount The total count.
	 */
	private void generateCounts(List<DataDivision> dataDivisionList,
			int totalCount) {

		// First pass at division.
		int countSofar = 0;
		for (DataDivision division : dataDivisionList) {
			int count = (int) (division.getPercent() * totalCount);
			division.setCount(count);
			countSofar += count;
		}
		// Adjust any remaining count
		int remaining = totalCount - countSofar;
		while (remaining-- > 0) {
			int idx = this.rnd.nextInt(dataDivisionList.size());
			DataDivision div = dataDivisionList.get(idx);
			div.setCount(div.getCount() + 1);
		}
	}

	/**
	 * @return the shuffle
	 */
	public boolean isShuffle() {
		return shuffle;
	}

	/**
	 * @return the rnd
	 */
	public GenerateRandom getRandom() {
		return rnd;
	}

}
