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

import java.util.List;

import org.encog.EncogError;
import org.encog.mathutil.randomize.generate.GenerateRandom;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.division.DataDivision;
import org.encog.ml.data.versatile.division.PerformDataDivision;
import org.encog.ml.data.versatile.normalizers.strategies.NormalizationStrategy;
import org.encog.ml.data.versatile.sources.VersatileDataSource;

/**
 * The versatile dataset supports several advanced features. 1. it can directly
 * read and normalize from a CSV file. 2. It supports virtual time-boxing for
 * time series data (the data is NOT expanded in memory). 3. It can easily be
 * segmented into smaller datasets.
 */
public class VersatileMLDataSet extends MatrixMLDataSet {

	/**
	 * The source that data is being pulled from.
	 */
	private VersatileDataSource source;
	
	/**
	 * The normalization helper.
	 */
	private NormalizationHelper helper = new NormalizationHelper();

	/**
	 * The number of rows that were analyzed.
	 */
	private int analyzedRows;

	/**
	 * Construct the data source. 
	 * @param theSource The data source.
	 */
	public VersatileMLDataSet(VersatileDataSource theSource) {
		this.source = theSource;
	}

	/**
	 * Find the index of a column.
	 * @param colDef The column.
	 * @return The column index.
	 */
	private int findIndex(ColumnDefinition colDef) {
		if (colDef.getIndex() != -1) {
			return colDef.getIndex();
		}

		int index = this.source.columnIndex(colDef.getName());
		colDef.setIndex(index);

		if (index == -1) {
			throw new EncogError("Can't find column");
		}

		return index;
	}

	/**
	 * Analyze the input and determine max, min, mean, etc.
	 */
	public void analyze() {
		String[] line;

		// Collect initial stats: sums (for means), highs, lows.
		this.source.rewind();
		int c = 0;
		while ((line = this.source.readLine()) != null) {
			c++;
			for (int i = 0; i < this.helper.getSourceColumns().size(); i++) {
				ColumnDefinition colDef = this.helper.getSourceColumns().get(i);
				int index = findIndex(colDef);
				String value = line[index];
				colDef.analyze(value);
			}
		}
		this.analyzedRows = c;

		// Calculate the means, and reset for sd calc.
		for (ColumnDefinition colDef : this.helper.getSourceColumns()) {
			// Only calculate mean/sd for continuous columns.
			if (colDef.getDataType() == ColumnType.continuous) {
				colDef.setMean(colDef.getMean() / colDef.getCount());
				colDef.setSd(0);
			}
		}

		// Sum the standard deviation
		this.source.rewind();
		while ((line = this.source.readLine()) != null) {
			for (int i = 0; i < this.helper.getSourceColumns().size(); i++) {
				ColumnDefinition colDef = this.helper.getSourceColumns().get(i);
				String value = line[colDef.getIndex()];
				if (colDef.getDataType() == ColumnType.continuous) {
					double d = this.helper.parseDouble(value);
					d = colDef.getMean() - d;
					d = d * d;
					colDef.setSd(colDef.getSd() + d);
				}
			}
		}

		// Calculate the standard deviations.
		for (ColumnDefinition colDef : this.helper.getSourceColumns()) {
			// Only calculate sd for continuous columns.
			if (colDef.getDataType() == ColumnType.continuous) {
				colDef.setSd(Math.sqrt(colDef.getSd() / colDef.getCount()));
			}
		}
	}

	/**
	 * Normalize the data set, and allocate memory to hold it.
	 */
	public void normalize() {
		NormalizationStrategy strat = this.helper.getNormStrategy();

		if (strat == null) {
			throw new EncogError(
					"Please choose a model type first, with selectMethod.");
		}

		int normalizedInputColumns = this.helper
				.calculateNormalizedInputCount();
		int normalizedOutputColumns = this.helper
				.calculateNormalizedOutputCount();

		int normalizedColumns = normalizedInputColumns
				+ normalizedOutputColumns;
		setCalculatedIdealSize(normalizedOutputColumns);
		setCalculatedInputSize(normalizedInputColumns);

		setData(new double[this.analyzedRows][normalizedColumns]);

		this.source.rewind();
		String[] line;
		int row = 0;
		while ((line = this.source.readLine()) != null) {
			int column = 0;
			for (ColumnDefinition colDef : this.helper.getInputColumns()) {
				int index = findIndex(colDef);
				String value = line[index];

				column = this.helper.normalizeToVector(colDef, column,
						getData()[row], true, value);
			}

			for (ColumnDefinition colDef : this.helper.getOutputColumns()) {
				int index = findIndex(colDef);
				String value = line[index];

				column = this.helper.normalizeToVector(colDef, column,
						getData()[row], false, value);
			}
			row++;
		}
	}

	/**
	 * Define a source column. Used when the file does not contain headings.
	 * @param name The name of the column.
	 * @param index The index of the column.
	 * @param colType The column type.
	 * @return The column definition.
	 */
	public ColumnDefinition defineSourceColumn(String name, int index,
			ColumnType colType) {
		return this.helper.defineSourceColumn(name, index, colType);
	}

	/**
	 * @return the helper
	 */
	public NormalizationHelper getNormHelper() {
		return helper;
	}

	/**
	 * @param helper
	 *            the helper to set
	 */
	public void setNormHelper(NormalizationHelper helper) {
		this.helper = helper;
	}

	/**
	 * Divide, and optionally shuffle, the dataset.
	 * @param dataDivisionList The desired divisions.
	 * @param shuffle True, if we should shuffle.
	 * @param rnd Random number generator, often with a specific seed.
	 */
	public void divide(List<DataDivision> dataDivisionList, boolean shuffle,
			GenerateRandom rnd) {
		if (getData() == null) {
			throw new EncogError(
					"Can't divide, data has not yet been generated/normalized.");
		}

		PerformDataDivision divide = new PerformDataDivision(shuffle, rnd);
		divide.perform(dataDivisionList, this, getCalculatedInputSize(),
				getCalculatedIdealSize());

	}

	/**
	 * Define an output column.
	 * @param col The output column.
	 */
	public void defineOutput(ColumnDefinition col) {
		this.helper.getOutputColumns().add(col);
	}

	/**
	 * Define an input column.
	 * @param col The input column.
	 */
	public void defineInput(ColumnDefinition col) {
		this.helper.getInputColumns().add(col);
	}

	/**
	 * Define a single column as an output column, all others as inputs.
	 * @param outputColumn The output column.
	 */
	public void defineSingleOutputOthersInput(ColumnDefinition outputColumn) {
		this.helper.clearInputOutput();

		for (ColumnDefinition colDef : this.helper.getSourceColumns()) {
			if (colDef == outputColumn) {
				defineOutput(colDef);
			} else if (colDef.getDataType() != ColumnType.ignore) {
				defineInput(colDef);
			}
		}
	}

	/**
	 * Define a source column.
	 * @param name The name of the source column.
	 * @param colType The column type.
	 * @return The column definition.
	 */
	public ColumnDefinition defineSourceColumn(String name, ColumnType colType) {
		return this.helper.defineSourceColumn(name, -1, colType);
	}

	/**
	 * Define multiple output columns, all others as inputs.
	 * @param outputColumns The output columns.
	 */
	public void defineMultipleOutputsOthersInput(ColumnDefinition[] outputColumns) {
		this.helper.clearInputOutput();

		for (ColumnDefinition colDef : this.helper.getSourceColumns()) {
			boolean isOutput = false;
			for(ColumnDefinition col : outputColumns) {
				if( col==colDef) {
					isOutput = true;
				}
			}
			
			if (  isOutput) {
				defineOutput(colDef);
			} else if (colDef.getDataType() != ColumnType.ignore) {
				defineInput(colDef);
			}
		}
	}

}
