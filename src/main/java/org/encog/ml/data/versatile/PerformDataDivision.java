package org.encog.ml.data.versatile;

import java.util.List;

import org.encog.mathutil.randomize.generate.GenerateRandom;
import org.encog.ml.data.MLDataSet;

public class PerformDataDivision {
	private final boolean shuffle;
	private final GenerateRandom rnd;

	public PerformDataDivision(boolean theShuffle, GenerateRandom theRandom) {
		this.shuffle = theShuffle;
		this.rnd = theRandom;
	}

	public void perform(List<DataDivision> dataDivisionList, double[][] data,
			int inputCount, int idealCount) {
		generateCounts(dataDivisionList, data.length);
		generateMasks(dataDivisionList);
		if (this.shuffle) {
			performShuffle(dataDivisionList, data.length);
		}
		createDividedDatasets(dataDivisionList, data, inputCount, idealCount);

	}

	private void createDividedDatasets(List<DataDivision> dataDivisionList,
			double[][] data, int inputCount, int idealCount) {
		for (DataDivision division : dataDivisionList) {
			MatrixMLDataSet dataset = new MatrixMLDataSet(data, inputCount,
					idealCount, division.getMask());
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

	private void generateMasks(List<DataDivision> dataDivisionList) {
		int idx = 0;
		for (DataDivision division : dataDivisionList) {
			division.allocateMask(division.getCount());
			for (int i = 0; i < division.getCount(); i++) {
				division.getMask()[i] = idx++;
			}
		}
	}

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
			int idx = this.rnd.nextInt(totalCount);
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
