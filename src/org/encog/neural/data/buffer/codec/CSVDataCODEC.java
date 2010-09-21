/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.neural.data.buffer.codec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.encog.engine.util.EngineArray;
import org.encog.neural.data.buffer.BufferedDataError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.csv.ReadCSV;

/**
 * A CODEC used to read/write data from/to a CSV data file. There are two
 * constructors provided, one is for reading, the other for writing. Make sure
 * you use the correct one for your intended purpose.
 * 
 * This CODEC is typically used with the BinaryDataLoader, to load external data
 * into the Encog binary training format.
 */
public class CSVDataCODEC implements DataSetCODEC {

	/**
	 * The external CSV file.
	 */
	private File file;

	/**
	 * The CSV format to use.
	 */
	private CSVFormat format;

	/**
	 * The size of the input data.
	 */
	private int inputCount;

	/**
	 * The size of the ideal data.
	 */
	private int idealCount;

	/**
	 * True, if headers are present in the CSV file.
	 */
	private boolean headers;

	/**
	 * The utility to assist in reading the CSV file.
	 */
	private ReadCSV readCSV;

	/**
	 * A file used to output the CSV file.
	 */
	private PrintStream output;

	/**
	 * Create a CODEC to load data from CSV to binary.
	 * 
	 * @param file
	 *            The CSV file to load.
	 * @param format
	 *            The format that the CSV file is in.
	 * @param headers
	 * 			True, if there are headers.
	 * @param inputCount
	 *            The number of input columns.
	 * @param idealCount
	 *            The number of ideal columns.
	 */
	public CSVDataCODEC(
			final File file, 
			final CSVFormat format, 
			final boolean headers,
			final int inputCount, final int idealCount) {
		if (this.inputCount != 0) {
			throw new BufferedDataError(
					"To export CSV, you must use the CSVDataCODEC constructor that does not specify input or ideal sizes.");
		}
		this.file = file;
		this.format = format;
		this.inputCount = inputCount;
		this.idealCount = idealCount;
		this.headers = headers;
	}

	/**
	 * Constructor to create CSV from binary..
	 * 
	 * @param file
	 *            The CSV file to create.
	 * @param format
	 *            The format for that CSV file.
	 */
	public CSVDataCODEC(final File file, final CSVFormat format) {
		this.file = file;
		this.format = format;
	}

	/**
	 * Read one record of data from a CSV file.
	 * 
	 * @param input
	 *            The input data array.
	 * @param ideal
	 *            The ideal data array.
	 * @return True, if there is more data to be read.
	 */
	@Override
	public boolean read(final double[] input, final double[] ideal) {
		if (this.readCSV.next()) {
			int index = 0;
			for (int i = 0; i < input.length; i++) {
				input[i] = readCSV.getDouble(index++);
			}

			for (int i = 0; i < ideal.length; i++) {
				ideal[i] = readCSV.getDouble(index++);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Write one record of data to a CSV file.
	 * 
	 * @param input
	 *            The input data array.
	 * @param ideal
	 *            The ideal data array.
	 */
	@Override
	public void write(final double[] input, final double[] ideal) {
		double[] record = new double[input.length + ideal.length];
		EngineArray.arrayCopy(input, record);
		EngineArray.arrayCopy(ideal, 0, record, input.length, ideal.length);
		StringBuilder result = new StringBuilder();
		NumberList.toList(this.format, result, record);
		this.output.println(result.toString());
	}

	/**
	 * Prepare to write to a CSV file.
	 * 
	 * @param recordCount
	 *            The total record count, that will be written.
	 * @param inputSize
	 *            The input size.
	 * @param idealSize
	 *            The ideal size.
	 */
	@Override
	public void prepareWrite(
				final int recordCount, 
				final int inputSize, 
				final int idealSize) {
		try {
			this.inputCount = inputSize;
			this.idealCount = idealSize;
			this.output = new PrintStream(new FileOutputStream(this.file));
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}

	/**
	 * Prepare to read from the CSV file.
	 */
	public void prepareRead() {
		if (this.inputCount == 0) {
			throw new BufferedDataError(
					"To import CSV, you must use the CSVDataCODEC constructor that specifies input and ideal sizes.");
		}
		this.readCSV = new ReadCSV(this.file.toString(), this.headers,
				this.format);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputSize() {
		return this.inputCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIdealSize() {
		return this.idealCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		if (this.readCSV != null) {
			this.readCSV.close();
			this.readCSV = null;
		}

		if (this.output != null) {
			this.output.close();
			this.output = null;
		}

	}
}
