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
package org.encog.ml.data.buffer.codec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.encog.ml.data.buffer.BufferedDataError;
import org.encog.util.EngineArray;
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
	private final File file;

	/**
	 * The CSV format to use.
	 */
	private final CSVFormat format;

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
	 * True, if a significance column is expected.
	 */
	private boolean expectSignificance;

	/**
	 * Constructor to create CSV from binary..
	 * 
	 * @param theFile
	 *            The CSV file to create.
	 * @param theFormat
	 *            The format for that CSV file.
	 * @param theExpectSignificance
	 * 			  True, if a significance column is expected.
	 */
	public CSVDataCODEC(final File theFile, final CSVFormat theFormat,
			final boolean theExpectSignificance) {
		this.file = theFile;
		this.format = theFormat;
		this.expectSignificance = theExpectSignificance;
	}

	/**
	 * Create a CODEC to load data from CSV to binary.
	 * 
	 * @param theFile
	 *            The CSV file to load.
	 * @param theFormat
	 *            The format that the CSV file is in.
	 * @param theHeaders
	 *            True, if there are headers.
	 * @param theInputCount
	 *            The number of input columns.
	 * @param theIdealCount
	 *            The number of ideal columns.
	 * @param theExpectSignificance
	 * 			  True, if a significance column is expected.
	 */
	public CSVDataCODEC(final File theFile, final CSVFormat theFormat,
			final boolean theHeaders, final int theInputCount,
			final int theIdealCount, final boolean theExpectSignificance) {
		if (this.inputCount != 0) {
			throw new BufferedDataError(
					"To export CSV, you must use the CSVDataCODEC "
							+ "constructor that does not specify input or ideal sizes.");
		}
		this.file = theFile;
		this.format = theFormat;
		this.inputCount = theInputCount;
		this.idealCount = theIdealCount;
		this.headers = theHeaders;
		this.expectSignificance = theExpectSignificance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void close() {
		if (this.readCSV != null) {
			this.readCSV.close();
			this.readCSV = null;
		}

		if (this.output != null) {
			this.output.close();
			this.output = null;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getIdealSize() {
		return this.idealCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getInputSize() {
		return this.inputCount;
	}

	/**
	 * Prepare to read from the CSV file.
	 */
	@Override
	public final void prepareRead() {
		if (this.inputCount == 0) {
			throw new BufferedDataError("To import CSV, you must use the "
					+ "CSVDataCODEC constructor that specifies input and "
					+ "ideal sizes.");
		}
		this.readCSV = new ReadCSV(this.file.toString(), this.headers,
				this.format);
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
	public final void prepareWrite(final int recordCount, final int inputSize,
			final int idealSize) {
		try {
			this.inputCount = inputSize;
			this.idealCount = idealSize;
			this.output = new PrintStream(new FileOutputStream(this.file));
		} catch (final IOException ex) {
			throw new BufferedDataError(ex);
		}
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
	public final boolean read(final double[] input, final double[] ideal,
			double[] significance) {
		if (this.readCSV.next()) {
			int index = 0;
			for (int i = 0; i < input.length; i++) {
				input[i] = this.readCSV.getDouble(index++);
			}

			for (int i = 0; i < ideal.length; i++) {
				ideal[i] = this.readCSV.getDouble(index++);
			}

			if (this.expectSignificance) {
				significance[0] = this.readCSV.getDouble(index++);
			} else {
				significance[0] = 1.0;
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
	public final void write(final double[] input, final double[] ideal,
			double significance) {
		if (this.expectSignificance) {
			final double[] record = new double[input.length + ideal.length + 1];
			EngineArray.arrayCopy(input, record);
			EngineArray.arrayCopy(ideal, 0, record, input.length, ideal.length);
			record[record.length - 1] = significance;
			final StringBuilder result = new StringBuilder();
			NumberList.toList(this.format, result, record);
			this.output.println(result.toString());
		} else {
			final double[] record = new double[input.length + ideal.length];
			EngineArray.arrayCopy(input, record);
			EngineArray.arrayCopy(ideal, 0, record, input.length, ideal.length);
			final StringBuilder result = new StringBuilder();
			NumberList.toList(this.format, result, record);
			this.output.println(result.toString());
		}
	}
}
