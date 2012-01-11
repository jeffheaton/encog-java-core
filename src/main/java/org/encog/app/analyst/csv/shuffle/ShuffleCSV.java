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
package org.encog.app.analyst.csv.shuffle;

import java.io.File;
import java.io.PrintWriter;

import org.encog.app.analyst.csv.basic.BasicFile;
import org.encog.app.analyst.csv.basic.LoadedRow;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Randomly shuffle the lines of a CSV file.
 */
public class ShuffleCSV extends BasicFile {
	
	/**
	 * The default buffer size.
	 */
	public static final int DEFAULT_BUFFER_SIZE = 5000;
	
	/**
	 * The buffer size.
	 */
	private int bufferSize;

	/**
	 * The buffer.
	 */
	private LoadedRow[] buffer;

	/**
	 * Remaining in the buffer.
	 */
	private int remaining;

	/**
	 * Construct the object.
	 */
	public ShuffleCSV() {
		setBufferSize(DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Analyze the neural network.
	 * 
	 * @param inputFile
	 *            The input file.
	 * @param headers
	 *            True, if there are headers.
	 * @param format
	 *            The format of the CSV file.
	 */
	public final void analyze(final File inputFile, final boolean headers,
			final CSVFormat format) {
		setInputFilename(inputFile);
		setExpectInputHeaders(headers);
		setInputFormat(format);

		setAnalyzed(true);

		performBasicCounts();
	}

	/**
	 * @return The buffer size. This is how many rows of data are loaded(and
	 *         randomized), at a time. The default is 5,000.
	 */
	public final int getBufferSize() {
		return this.bufferSize;
	}

	/**
	 * Get the next row from the underlying CSV file.
	 * 
	 * @param csv
	 *            The underlying CSV file.
	 * @return The loaded row.
	 */
	private LoadedRow getNextRow(final ReadCSV csv) {
		if (this.remaining == 0) {
			loadBuffer(csv);
		}

		while (this.remaining > 0) {
			final int index = RangeRandomizer.randomInt(0, this.bufferSize - 1);
			if (this.buffer[index] != null) {
				final LoadedRow result = this.buffer[index];
				this.buffer[index] = null;
				this.remaining--;
				return result;
			}
		}
		return null;
	}

	/**
	 * Load the buffer from the underlying file.
	 * 
	 * @param csv
	 *            The CSV file to load from.
	 */
	private void loadBuffer(final ReadCSV csv) {
		for (int i = 0; i < this.buffer.length; i++) {
			this.buffer[i] = null;
		}

		int index = 0;
		while (csv.next() && (index < this.bufferSize) && !shouldStop()) {
			final LoadedRow row = new LoadedRow(csv);
			this.buffer[index++] = row;
		}

		this.remaining = index;
	}

	/**
	 * Process, and generate the output file.
	 * 
	 * @param outputFile
	 *            The output file.
	 */
	public final void process(final File outputFile) {
		validateAnalyzed();

		final ReadCSV csv = new ReadCSV(getInputFilename().toString(),
				isExpectInputHeaders(), getFormat());
		LoadedRow row;

		final PrintWriter tw = prepareOutputFile(outputFile);

		resetStatus();
		while ((row = getNextRow(csv)) != null) {
			writeRow(tw, row);
			updateStatus(false);
		}
		reportDone(false);
		tw.close();
		csv.close();
	}

	/**
	 * Set the buffer size.
	 * 
	 * @param s
	 *            The new buffer size.
	 */
	public final void setBufferSize(final int s) {
		this.bufferSize = s;
		this.buffer = new LoadedRow[this.bufferSize];
	}
}
