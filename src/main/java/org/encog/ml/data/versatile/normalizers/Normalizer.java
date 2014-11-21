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
package org.encog.ml.data.versatile.normalizers;

import java.io.Serializable;

import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.columns.ColumnDefinition;

/**
 * The normalizer interface defines how to normalize a column.  The source of the
 * normalization can be either string or double.
 */
public interface Normalizer extends Serializable { 

	/**
	 * Determine the normalized size of the specified column.
	 * @param colDef The column to check.
	 * @return The size of the column normalized.
	 */
	int outputSize(ColumnDefinition colDef);

	/**
	 * Normalize a column from a string. The output will go to an array, starting at outputColumn.
	 * @param colDef The column that is being normalized.
	 * @param value The value to normalize.
	 * @param outputData The array to output to.
	 * @param outputIndex The index to start at in outputData.
	 * @return The new index (in outputData) that we've moved to.
	 */
	int normalizeColumn(ColumnDefinition colDef, String value,
			double[] outputData, int outputIndex);

	/**
	 * Normalize a column from a double. The output will go to an array, starting at outputColumn.
	 * @param colDef The column that is being normalized.
	 * @param value The value to normalize.
	 * @param outputData The array to output to.
	 * @param outputIndex The index to start at in outputData.
	 * @return The new index (in outputData) that we've moved to.
	 */
	int normalizeColumn(ColumnDefinition colDef, double value,
			double[] outputData, int outputIndex);
	
	/**
	 * Denormalize a value.
	 * @param colDef The column to denormalize.
	 * @param data The data to denormalize.
	 * @param dataIndex The starting location inside data.
	 * @return The denormalized value.
	 */
	String denormalizeColumn(ColumnDefinition colDef, MLData data,
			int dataIndex);
}
