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

import org.encog.EncogError;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.columns.ColumnDefinition;

/**
 * Normalize ordinal/nominal values to a single value that is simply the index
 * of the class in the list. For example, "one", "two", "three" normalizes to
 * 0,1,2.
 */
public class IndexedNormalizer implements Normalizer {
 	private static final long serialVersionUID = 1L;

 	@Override
 	public boolean equals(Object obj) {
 		return ( obj instanceof IndexedNormalizer ); 
 	}
 	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int outputSize(ColumnDefinition colDef) {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int normalizeColumn(ColumnDefinition colDef, String value,
			double[] outputData, int outputColumn) {

		if (!colDef.getClasses().contains(value)) {
			throw new EncogError("Undefined value: " + value);
		}

		outputData[outputColumn] = colDef.getClasses().indexOf(value);
		return outputColumn + 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String denormalizeColumn(ColumnDefinition colDef, MLData data,
			int dataColumn) {

		return colDef.getClasses().get((int) data.getData(dataColumn));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int normalizeColumn(ColumnDefinition colDef, double value,
			double[] outputData, int outputColumn) {
		throw new EncogError(
				"Can't use an indexed normalizer on a continuous value: "
						+ value);
	}

}
