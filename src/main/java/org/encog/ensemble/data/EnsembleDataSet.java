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
package org.encog.ensemble.data;

import java.util.ArrayList;
import java.util.Iterator;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;

/**
 * @author nitbix
 *
 */
public class EnsembleDataSet implements MLDataSet {

	private ArrayList<MLDataPair> data;
	private int idealSize;
	private int inputSize;

	public EnsembleDataSet(int inputSize, int idealSize) {
		this.idealSize = idealSize;
		this.inputSize = inputSize;
		data = new ArrayList<MLDataPair>();
	}

	public EnsembleDataSet(MLDataSet mlds) {
		this.idealSize = mlds.getIdealSize();
		this.inputSize = mlds.getInputSize();
		Iterator<MLDataPair> it = mlds.iterator();
		data = new ArrayList<MLDataPair>();
		while(it.hasNext()) {
			data.add(it.next());
		}
	}

	@Override
	public int getIdealSize() {
		return idealSize;
	}

	@Override
	public int getInputSize() {
		return inputSize;
	}

	@Override
	public boolean isSupervised() {
		return true;
	}

	@Override
	public long getRecordCount() {
		return data.size();
	}

	@Override
	public void getRecord(long index, MLDataPair pair) {
		final MLDataPair source = this.data.get((int) index);
		pair.setInputArray(source.getInputArray());
		if (pair.getIdealArray() != null) {
			pair.setIdealArray(source.getIdealArray());
		}
	}

	@Override
	public MLDataSet openAdditional() {
		EnsembleDataSet copy = new EnsembleDataSet(idealSize,inputSize);
		for (MLDataPair line: data) {
			BasicMLDataPair newLine = new BasicMLDataPair(line.getInput(), line.getIdeal());
			copy.add(newLine);
		}
		return copy;
	}

	@Override
	public void add(MLData data1) {
		BasicMLDataPair mlP = new BasicMLDataPair(data1);
		data.add(mlP);
	}

	@Override
	public void add(MLData inputData, MLData idealData) {
		BasicMLDataPair mlP = new BasicMLDataPair(inputData, idealData);
		data.add(mlP);
	}

	@Override
	public void add(MLDataPair inputData) {
		data.add(inputData);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public MLDataPair get(int index) {
		return data.get(index);
	}

	@Override
	public Iterator<MLDataPair> iterator() {
		return data.iterator();
	}

}
