/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.app.csv.evaluate;

import java.io.File;
import java.io.PrintWriter;

import org.encog.app.csv.EncogCSVError;
import org.encog.app.csv.basic.BasicFile;
import org.encog.app.csv.basic.LoadedRow;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLDataArray;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Evaluate the data in a CSV file using a MLMethod.
 */
public class EvaluateCSV extends BasicFile {

	/**
	 * Analyze the data. This counts the records and prepares the data to be
	 * processed.
	 * 
	 * @param inputFile
	 *            The input file to process.
	 * @param headers
	 *            True, if headers are present.
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
	 * Process the evaluation.
	 * @param outputFile The output file.
	 * @param method The machine learning method to use.
	 */
	public final void process(final File outputFile, 
			final MLRegression method) {

		final ReadCSV csv = new ReadCSV(getInputFilename().toString(),
				isExpectInputHeaders(), getInputFormat());

		final PrintWriter tw = prepareOutputFile(outputFile);
		final MLData input = new BasicMLDataArray(method.getInputCount());

		final int methodCount = method.getInputCount()
				+ method.getOutputCount();
		if (methodCount != getColumnCount()) {
			throw new EncogCSVError("ML Method expects " + methodCount
					+ ", however " + getColumnCount()
					+ " columes are in the file.");
		}

		resetStatus();
		while (csv.next() && !shouldStop()) {
			updateStatus(false);
			final LoadedRow row = new LoadedRow(csv, method.getOutputCount());
			for (int i = 0; i < method.getInputCount(); i++) {
				final double d = getInputFormat().parse(row.getData()[i]);
				input.setData(i, d);
			}
			final MLData output = method.compute(input);

			for (int i = 0; i < output.size(); i++) {
				row.getData()[i + method.getInputCount()] = getInputFormat()
						.format(output.getData(i), getPrecision());
			}

			writeRow(tw, row);
		}
		reportDone(false);
		tw.close();
		csv.close();

	}

}
