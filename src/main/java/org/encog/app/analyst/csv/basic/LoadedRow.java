/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.app.analyst.csv.basic;

import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * A row of a CSV file loaded to memory. This class is used internally by many
 * of the Encog quant classes.
 */
public class LoadedRow {

	/**
	 * The row data.
	 */
	private final String[] data;

	/**
	 * Load a row from the specified CSV file.
	 * 
	 * @param csv
	 *            The CSV file to use.
	 */
	public LoadedRow(final ReadCSV csv) {
		this(csv, 0);
	}

	/**
	 * Construct a loaded row.
	 * @param csv The CSV file to use.
	 * @param extra The number of extra columns to add.
	 */
	public LoadedRow(final ReadCSV csv, final int extra) {
		final int count = csv.getColumnCount();
		this.data = new String[count + extra];
		for (int i = 0; i < count; i++) {
			this.data[i] = csv.get(i);
		}
	}
	
	public LoadedRow(CSVFormat csvFormat, final double[] d, final int extra) {
		final int count = d.length;
		this.data = new String[count + extra];
		for (int i = 0; i < count; i++) {
			this.data[i] = csvFormat.format(d[i], 5) + d[i];
		}
	}

	/**
	 * @return The row data.
	 */
	public final String[] getData() {
		return this.data;
	}
}
