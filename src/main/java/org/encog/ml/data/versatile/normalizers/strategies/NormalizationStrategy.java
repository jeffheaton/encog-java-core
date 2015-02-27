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
package org.encog.ml.data.versatile.normalizers.strategies;

import java.io.Serializable;

import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.columns.ColumnDefinition;

/**
 * Defines the interface to a normalization strategy.
 */
public interface NormalizationStrategy extends Serializable { 

	/**
	 * Calculate how many elements a column will normalize into.
	 * @param colDef The column definition.
	 * @param isInput True, if this is an input column.
	 * @return The number of elements needed to normalize this column.
	 */
	int normalizedSize(ColumnDefinition colDef, boolean isInput);

	/**
	 * Normalize a column, with a string input.
	 * @param colDef The column definition.
	 * @param isInput True, if this is an input column.
	 * @param value The value to normalize.
	 * @param outpuData The output data.
	 * @param outputColumn The element to begin outputing to.
	 * @return The new output element, advanced by the correct amount.
	 */
	int normalizeColumn(ColumnDefinition colDef, boolean isInput, String value,
			double[] outpuData, int outputColumn);

	/**
	 * Normalize a column, with a double input.
	 * @param colDef The column definition.
	 * @param isInput True, if this is an input column.
	 * @param output The output data.
	 * @param idx The element to begin outputing to.
	 * @return The new output element, advanced by the correct amount.
	 */
	String denormalizeColumn(ColumnDefinition colDef, boolean isInput, MLData output,
			int idx);

	/**
	 * Normalize a column, with a double value.
	 * @param colDef The column definition.
	 * @param isInput True, if this is an input column.
	 * @param value The value to normalize.
	 * @param outpuData The output data.
	 * @param outputColumn The element to begin outputing to.
	 * @return The new output element, advanced by the correct amount.
	 */
	int normalizeColumn(ColumnDefinition colDef, boolean isInput, double value,
			double[] outpuData, int outputColumn);

}
