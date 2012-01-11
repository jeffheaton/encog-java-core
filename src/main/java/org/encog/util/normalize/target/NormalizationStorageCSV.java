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
package org.encog.util.normalize.target;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.normalize.DataNormalization;
import org.encog.util.normalize.NormalizationError;

/**
 * Store normalized data to a CSV file.
 */
public class NormalizationStorageCSV implements NormalizationStorage {

	/**
	 * The output file.
	 */
	private File outputFile;
	
	/**
	 * The output writer.
	 */
	private transient PrintWriter output;
	
	/**
	 * The CSV format to use.
	 */
	private CSVFormat format;

	/**
	 * Construct a CSV storage object from the specified file.
	 * @param format The format to use.
	 * @param file The file to write the CSV to.
	 */
	public NormalizationStorageCSV(final CSVFormat format, final File file) {
		this.format = format;
		this.outputFile = file;
	}
	
	public NormalizationStorageCSV()
	{
		this.format = CSVFormat.EG_FORMAT;
	}

	/**
	 * Construct a CSV storage object from the specified file.
	 * @param file The file to write the CSV to.
	 */
	public NormalizationStorageCSV(final File file) {
		this.format = CSVFormat.ENGLISH;
		this.outputFile = file;
	}

	/**
	 * Close the CSV file.
	 */
	public void close() {
		this.output.close();
	}

	/**
	 * Open the CSV file.
	 */
	public void open(DataNormalization norm) {
		try {
			final FileWriter outFile = new FileWriter(this.outputFile);
			this.output = new PrintWriter(outFile);
		} catch (final IOException e) {
			throw (new NormalizationError(e));
		}
	}

	/**
	 * Write an array.
	 * 
	 * @param data
	 *            The data to write.
	 * @param inputCount
	 *            How much of the data is input.
	 */
	public void write(final double[] data, final int inputCount) {
		final StringBuilder result = new StringBuilder();
		NumberList.toList(this.format, result, data);
		this.output.println(result.toString());
	}

}
