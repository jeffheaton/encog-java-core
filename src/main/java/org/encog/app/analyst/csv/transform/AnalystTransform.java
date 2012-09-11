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
package org.encog.app.analyst.csv.transform;

import java.io.File;
import java.io.PrintWriter;

import org.encog.app.analyst.csv.basic.BasicFile;
import org.encog.app.analyst.csv.basic.LoadedRow;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Perform many different types of transformations on a CSV.
 */
public class AnalystTransform extends BasicFile {

	/**
	 * The buffer.
	 */
	private LoadedRow[] buffer;

	/**
	 * Construct the object.
	 */
	public AnalystTransform() {
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
	public void analyze(final File inputFile, final boolean headers,
			final CSVFormat format) {
		setInputFilename(inputFile);
		setExpectInputHeaders(headers);
		setInputFormat(format);

		setAnalyzed(true);

		performBasicCounts();
	}

	/**
	 * Get the next row from the underlying CSV file.
	 * 
	 * @param csv
	 *            The underlying CSV file.
	 * @return The loaded row.
	 */
	private LoadedRow getNextRow(final ReadCSV csv) {		
		if( csv.next() ) {
			return new LoadedRow(csv);
		} else {
			return null;
		}		
	}


	/**
	 * Process, and generate the output file.
	 * 
	 * @param outputFile
	 *            The output file.
	 */
	public void process(final File outputFile) {
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
}
