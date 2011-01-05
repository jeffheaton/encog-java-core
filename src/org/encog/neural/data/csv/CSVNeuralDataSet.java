/*
 * Encog(tm) Core v2.6 - Java Version
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
package org.encog.neural.data.csv;

import java.io.File;

import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.data.buffer.MemoryDataLoader;
import org.encog.neural.data.buffer.codec.CSVDataCODEC;
import org.encog.neural.data.buffer.codec.DataSetCODEC;
import org.encog.util.csv.CSVFormat;

/**
 * An implementation of the NeuralDataSet interface designed to provide a CSV
 * file to the neural network. This implementation uses the BasicNeuralData to
 * hold the data being read. This class has no ability to write CSV files. The
 * columns of the CSV file will specify both the input and ideal columns.
 * 
 * Because this class loads the CSV file to memory, it is quite fast, once the
 * data has been loaded.
 */
public class CSVNeuralDataSet extends BasicNeuralDataSet {

	/**
	 * The CSV filename to read from.
	 */
	private final String filename;

	/**
	 * The number of columns of input data.
	 */
	private final int inputSize;

	/**
	 * The number of columns of ideal data.
	 */
	private final int idealSize;

	/**
	 * The format of this CSV file.
	 */
	private final CSVFormat format;

	/**
	 * Specifies if headers are present on the first row.
	 */
	private final boolean headers;


	/**
	 * Construct this data set using a comma as a delimiter.
	 * 
	 * @param filename
	 *            The CSV filename to read.
	 * @param inputSize
	 *            The number of columns that make up the input set. *
	 * @param idealSize
	 *            The number of columns that make up the ideal set.
	 * @param headers
	 *            True if headers are present on the first line.
	 */
	public CSVNeuralDataSet(final String filename, final int inputSize,
			final int idealSize, final boolean headers) {
		this(filename, inputSize, idealSize, headers, CSVFormat.ENGLISH);
	}

	/**
	 * Construct this data set using a comma as a delimiter.
	 * 
	 * @param filename
	 *            The CSV filename to read.
	 * @param inputSize
	 *            The number of columns that make up the input set. *
	 * @param idealSize
	 *            The number of columns that make up the ideal set.
	 * @param headers
	 *            True if headers are present on the first line.
	 * @param format
	 *            What CSV format to use.
	 */
	public CSVNeuralDataSet(final String filename, final int inputSize,
			final int idealSize, final boolean headers,
			final CSVFormat format) {
		this.filename = filename;
		this.inputSize = inputSize;
		this.idealSize = idealSize;
		this.format = format;
		this.headers = headers;
		
        DataSetCODEC codec = new CSVDataCODEC(new File(filename), format, headers, inputSize, idealSize);
        MemoryDataLoader load = new MemoryDataLoader(codec);
        load.setResult(this);
        load.external2Memory();
	}


	/**
	 * @return the filename
	 */
	public String getFilename() {
		return this.filename;
	}

	/**
	 * @return the delimiter
	 */
	public CSVFormat getFormat() {
		return this.format;
	}


}
