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
package org.encog.ml.data.specific;

import java.io.File;

import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
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
public class CSVNeuralDataSet extends BasicMLDataSet {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The CSV filename to read from.
	 */
	private final String filename;

	/**
	 * The format of this CSV file.
	 */
	private final CSVFormat format;

	/**
	 * Construct this data set using a comma as a delimiter.
	 * 
	 * @param theFilename
	 *            The CSV filename to read.
	 * @param theInputSize
	 *            The number of columns that make up the input set. *
	 * @param theIdealSize
	 *            The number of columns that make up the ideal set.
	 * @param theHeaders
	 *            True if headers are present on the first line.
	 */
	public CSVNeuralDataSet(
			final String theFilename, 
			final int theInputSize,
			final int theIdealSize, 
			final boolean theHeaders) {
		this(theFilename, theInputSize, theIdealSize, theHeaders, 
				CSVFormat.ENGLISH,false);
	}

	/**
	 * Construct this data set using a comma as a delimiter.
	 * 
	 * @param theFilename
	 *            The CSV filename to read.
	 * @param theInputSize
	 *            The number of columns that make up the input set. *
	 * @param theIdealSize
	 *            The number of columns that make up the ideal set.
	 * @param theHeaders
	 *            True if headers are present on the first line.
	 * @param theFormat
	 *            What CSV format to use.
	 * @param theFormat
	 *            True, if there is a significance column.
	 */
	public CSVNeuralDataSet(
			final String theFilename, 
			final int theInputSize,
			final int theIdealSize, 
			final boolean theHeaders, 
			final CSVFormat theFormat,
			final boolean significance) {
		this.filename = theFilename;
		this.format = theFormat;

		final DataSetCODEC codec = new CSVDataCODEC(new File(filename), format,
				theHeaders, theInputSize, theIdealSize, significance);
		final MemoryDataLoader load = new MemoryDataLoader(codec);
		load.setResult(this);
		load.external2Memory();
	}

	/**
	 * @return the filename
	 */
	public final String getFilename() {
		return this.filename;
	}

	/**
	 * @return the delimiter
	 */
	public final CSVFormat getFormat() {
		return this.format;
	}

}
