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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.missing.MissingHandler;
import org.encog.ml.data.versatile.normalizers.strategies.NormalizationStrategy;
import org.encog.util.csv.CSVFormat;

/**
 * This class is used to perform normalizations for methods trained with the
 * versatile dataset.
 */
public class NormalizationHelper implements Serializable {
	/**
	 * The source columns from the original file. These are then added to the
	 * input and output columns.
	 */
	private List<ColumnDefinition> sourceColumns = new ArrayList<ColumnDefinition>();

	/**
	 * The columns, from the source columns, used for input to the model.
	 */
	private List<ColumnDefinition> inputColumns = new ArrayList<ColumnDefinition>();

	/**
	 * The columns, from the source columns, used for output from the model.
	 */
	private List<ColumnDefinition> outputColumns = new ArrayList<ColumnDefinition>();

	/**
	 * The normalizaton strategy to use.
	 */
	private NormalizationStrategy normStrategy;

	/**
	 * The CSV format to use.
	 */
	private CSVFormat format = CSVFormat.ENGLISH;

	/**
	 * What to do with unknown values.
	 */
	private List<String> unknownValues = new ArrayList<String>();

	/**
	 * The missing column handlers.
	 */
	private Map<ColumnDefinition, MissingHandler> missingHandlers = new HashMap<ColumnDefinition, MissingHandler>();

	/**
	 * @return the sourceColumns
	 */
	public List<ColumnDefinition> getSourceColumns() {
		return sourceColumns;
	}

	/**
	 * @param sourceColumns
	 *            the sourceColumns to set
	 */
	public void setSourceColumns(List<ColumnDefinition> sourceColumns) {
		this.sourceColumns = sourceColumns;
	}

	/**
	 * @return the inputColumns
	 */
	public List<ColumnDefinition> getInputColumns() {
		return inputColumns;
	}

	/**
	 * @param inputColumns
	 *            the inputColumns to set
	 */
	public void setInputColumns(List<ColumnDefinition> inputColumns) {
		this.inputColumns = inputColumns;
	}

	/**
	 * @return the outputColumns
	 */
	public List<ColumnDefinition> getOutputColumns() {
		return outputColumns;
	}

	/**
	 * @param outputColumns
	 *            the outputColumns to set
	 */
	public void setOutputColumns(List<ColumnDefinition> outputColumns) {
		this.outputColumns = outputColumns;
	}

	/**
	 * @return the normStrategy
	 */
	public NormalizationStrategy getNormStrategy() {
		return normStrategy;
	}

	/**
	 * @param normStrategy
	 *            the normStrategy to set
	 */
	public void setNormStrategy(NormalizationStrategy normStrategy) {
		this.normStrategy = normStrategy;
	}

	/**
	 * Add a source column. These define the raw input.
	 * 
	 * @param def
	 *            The column definition.
	 */
	public void addSourceColumn(ColumnDefinition def) {
		this.sourceColumns.add(def);
		def.setOwner(this);
	}

	/**
	 * Define a source column. These define the raw input. Use this function if
	 * you know the index of the column in a non-header file.
	 * 
	 * @param name
	 *            The name of the column.
	 * @param index
	 *            The index of the column, needed for non-headered files.
	 * @param colType
	 *            The column type.
	 * @return The column definition
	 */
	public ColumnDefinition defineSourceColumn(String name, int index,
			ColumnType colType) {
		ColumnDefinition result = new ColumnDefinition(name, colType);
		result.setIndex(index);
		addSourceColumn(result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[NormalizationHelper:\n");
		for (ColumnDefinition colDef : this.sourceColumns) {
			result.append(colDef.toString());
			result.append("\n");
		}
		result.append("]");
		return result.toString();
	}

	/**
	 * Clear the input/output columns, but not the source columns.
	 */
	public void clearInputOutput() {
		this.inputColumns.clear();
		this.outputColumns.clear();

	}

	/**
	 * Normalize a single input column.
	 * 
	 * @param i
	 *            The column definition index (from the input columns).
	 * @param value
	 *            The value to normalize.
	 * @return The normalized result.
	 */
	public double[] normalizeInputColumn(int i, String value) {
		ColumnDefinition colDef = this.inputColumns.get(i);
		double[] result = new double[this.normStrategy.normalizedSize(colDef,
				true)];
		this.normStrategy.normalizeColumn(colDef, true, value, result, 0);
		return result;
	}

	/**
	 * Normalize a single output column.
	 * 
	 * @param i
	 *            The column definition index (from the output columns).
	 * @param value
	 *            The value to normalize.
	 * @return The normalized result.
	 */
	public double[] normalizeOutputColumn(int i, String value) {
		ColumnDefinition colDef = this.outputColumns.get(i);
		double[] result = new double[this.normStrategy.normalizedSize(colDef,
				false)];
		this.normStrategy.normalizeColumn(colDef, false, value, result, 0);
		return result;
	}

	/**
	 * @return The number of elements the input will normalize to.
	 */
	public int calculateNormalizedInputCount() {
		int normalizedInputColumns = 0;

		for (ColumnDefinition colDef : this.inputColumns) {
			normalizedInputColumns += this.normStrategy.normalizedSize(colDef,
					true);
		}

		return normalizedInputColumns;
	}

	/**
	 * @return The number of elements the output will normalize to.
	 */
	public int calculateNormalizedOutputCount() {
		int normalizedOutputColumns = 0;

		for (ColumnDefinition colDef : this.outputColumns) {
			normalizedOutputColumns += this.normStrategy.normalizedSize(colDef,
					false);
		}

		return normalizedOutputColumns;
	}

	/**
	 * Allocate a data item large enough to hold a single input vector.
	 * 
	 * @return The data element.
	 */
	public MLData allocateInputVector() {
		return allocateInputVector(1);
	}

	/**
	 * Allocate a data item large enough to hold several input vectors. This is
	 * normally used for timeslices.
	 * 
	 * @param multiplier
	 *            How many input vectors.
	 * @return The data element.
	 */
	public MLData allocateInputVector(int multiplier) {
		return new BasicMLData(calculateNormalizedInputCount() * multiplier);
	}

	/**
	 * Denormalize a complete output vector to an array of strings.
	 * @param output The data vector to denorm, the source.
	 * @return The denormalized vector.
	 */
	public String[] denormalizeOutputVectorToString(MLData output) {
		String[] result = new String[this.outputColumns.size()];

		int idx = 0;
		for (int i = 0; i < this.outputColumns.size(); i++) {
			ColumnDefinition colDef = this.outputColumns.get(i);
			result[i] = this.normStrategy.denormalizeColumn(colDef, false,
					output, idx);
			idx += this.normStrategy.normalizedSize(colDef, false);
		}

		return result;

	}

	/**
	 * Set the normalization strategy.
	 * @param strat The strategy.
	 */
	public void setStrategy(NormalizationStrategy strat) {
		this.normStrategy = strat;
	}

	/**
	 * @return the format
	 */
	public CSVFormat getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(CSVFormat format) {
		this.format = format;
	}

	/**
	 * @return the unknownValues
	 */
	public List<String> getUnknownValues() {
		return unknownValues;
	}

	/**
	 * Define the string that signifies an unknown value (eg "?")
	 * @param str The string for unknowns.
	 */
	public void defineUnknownValue(String str) {
		this.unknownValues.add(str);
	}

	/**
	 * Normalize a single column to the input vector.
	 * @param colDef The column to normalize.
	 * @param outputColumn The current position in the vector.
	 * @param output The vector to output to.
	 * @param isInput Is this an input column.
	 * @param value The value to normalize.
	 * @return The new current position in the vector.
	 */
	public int normalizeToVector(ColumnDefinition colDef, int outputColumn,
			double[] output, boolean isInput, String value) {
		MissingHandler handler = null;

		if (this.unknownValues.contains(value)) {
			if (!this.missingHandlers.containsKey(colDef)) {
				throw new EncogError(
						"Do not know how to process missing value \"" + value
								+ "\" in field: " + colDef.getName());
			}
			handler = this.missingHandlers.get(colDef);
		}

		if (colDef.getDataType() == ColumnType.continuous) {
			double d = parseDouble(value);
			if (handler != null) {
				d = handler.processDouble(colDef);
			}
			return this.normStrategy.normalizeColumn(colDef, isInput, d,
					output, outputColumn);
		} else {
			if (handler != null) {
				value = handler.processString(colDef);
			}
			return this.normStrategy.normalizeColumn(colDef, isInput, value,
					output, outputColumn);
		}
	}

	/**
	 * Parse a double, using the correct formatter.
	 * @param str The string.
	 * @return The double.
	 */
	public double parseDouble(String str) {
		return this.format.parse(str);
	}

	/**
	 * Define a missing value handler.
	 * @param colDef The column this handler applies to.
	 * @param handler The handler.
	 */
	public void defineMissingHandler(ColumnDefinition colDef,
			MissingHandler handler) {
		this.missingHandlers.put(colDef, handler);
		handler.init(this);
	}

	/**
	 * Normalize a string array to an input vector.
	 * @param line The unnormalized string array.
	 * @param data The output data.
	 * @param originalOrder Should the output be forced into the original column order?
	 */
	public void normalizeInputVector(String[] line, double[] data,
			boolean originalOrder) {
		int outputIndex = 0;
		int i = 0;
		for (ColumnDefinition colDef : this.inputColumns) {
			int idx;

			if (originalOrder) {
				idx = this.sourceColumns.indexOf(colDef);
			} else {
				idx = i;
			}
			outputIndex = normalizeToVector(colDef, outputIndex, data, true,
					line[idx]);
			i++;
		}
	}
}
