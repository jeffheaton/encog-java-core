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

package org.encog.ensemble.data.factories;

import java.util.ArrayList;

import org.encog.ensemble.data.EnsembleDataSet;
import org.encog.ml.data.MLDataSet;

public abstract class EnsembleDataSetFactory {

	protected MLDataSet dataSource = null;
	protected int dataSetSize;

	public EnsembleDataSetFactory(int dataSetSize) {
		setDataSetSize(dataSetSize);
	}

	public void setInputData(MLDataSet dataSource) {
		this.dataSource = dataSource;
		this.reload();
	}

	abstract public EnsembleDataSet getNewDataSet();

	public boolean hasSource() {
		return (dataSource != null);
	}

	public MLDataSet getInputData() {
		return this.dataSource;
	}

	public int getDataSetSize() {
		return dataSetSize;
	}

	public void setDataSetSize(int dataSetSize) {
		this.dataSetSize = dataSetSize;
	}

	public int getDataSourceSize() {
		return this.dataSource.size();
	}
	
	public MLDataSet getDataSource() {
		return this.dataSource;
	}
	
	public int getInputCount() {
		return this.dataSource.getInputSize();
	}

	public int getOutputCount() {
		return this.dataSource.getIdealSize();
	}

	public void setSignificance(ArrayList<Double> D) {
		for (int i = 0; i < dataSource.size(); i++)
			dataSource.get(i).setSignificance(D.get(i));
	}

	public ArrayList<Double> getSignificance() {
		ArrayList<Double> res = new ArrayList<Double>();
		for (int i = 0; i < dataSource.size(); i++)
			res.add(dataSource.get(i).getSignificance());
		return res;
	}
	public void reload() {
	}

}