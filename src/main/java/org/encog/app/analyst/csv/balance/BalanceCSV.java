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
package org.encog.app.analyst.csv.balance;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.encog.app.analyst.csv.basic.BasicFile;
import org.encog.app.analyst.csv.basic.LoadedRow;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Balance a CSV file. This utility is useful when you have several an
 * unbalanced training set. You may have a large number of one particular class,
 * and many fewer elements of other classes. This can hinder many Machine
 * Learning methods. This class can be used to balance the data.
 * 
 * Obviously this class cannot generate data. You must request how many items
 * you want per class. Some classes will have lower than this number if they
 * were already below the specified amount. Any class above this amount will be
 * trimmed to that amount.
 */
public class BalanceCSV extends BasicFile {
	/**
	 * Tracks the counts of each class.
	 */
	private Map<String, Integer> counts;

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
		this.setInputFilename(inputFile);
		setExpectInputHeaders(headers);
		setInputFormat(format);

		setAnalyzed(true);

		performBasicCounts();
	}

	/**
	 * Return a string that lists the counts per class.
	 * 
	 * @return The counts per class.
	 */
	public final String dumpCounts() {
		final StringBuilder result = new StringBuilder();
		for (final String key : this.counts.keySet()) {
			result.append(key);
			result.append(" : ");
			result.append(this.counts.get(key));
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * @return Tracks the counts of each class.
	 */
	public final Map<String, Integer> getCounts() {
		return this.counts;
	}

	/**
	 * Process and balance the data.
	 * 
	 * @param outputFile
	 *            The output file to write data to.
	 * @param targetField
	 *            The field that is being balanced, this field determines the
	 *            classes.
	 * @param countPer
	 *            The desired count per class.
	 */
	public final void process(final File outputFile, final int targetField,
			final int countPer) {
		validateAnalyzed();
		final PrintWriter tw = prepareOutputFile(outputFile);

		this.counts = new HashMap<String, Integer>();

		final ReadCSV csv = new ReadCSV(getInputFilename().toString(),
				isExpectInputHeaders(), getFormat());

		resetStatus();
		while (csv.next() && !shouldStop()) {
			final LoadedRow row = new LoadedRow(csv);
			updateStatus(false);
			final String key = row.getData()[targetField];
			int count;
			if (!this.counts.containsKey(key)) {
				count = 0;
			} else {
				count = this.counts.get(key);
			}

			if (count < countPer) {
				writeRow(tw, row);
				count++;
			}

			this.counts.put(key, count);
		}
		reportDone(false);
		csv.close();
		tw.close();
	}

}
