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
package org.encog.app.analyst.csv.sort;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.encog.app.analyst.csv.basic.BasicFile;
import org.encog.app.analyst.csv.basic.LoadedRow;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Used to sort a CSV file by one, or more, fields.
 */
public class SortCSV extends BasicFile {

	/**
	 * The loaded rows.
	 */
	private final List<LoadedRow> data = new ArrayList<LoadedRow>();

	/**
	 * The sort order.
	 */
	private final List<SortedField> sortOrder = new ArrayList<SortedField>();

	/**
	 * @return Used to specify the sort order.
	 */
	public final List<SortedField> getSortOrder() {
		return this.sortOrder;
	}

	/**
	 * Process, and sort the files.
	 * 
	 * @param inputFile
	 *            The input file.
	 * @param outputFile
	 *            The output file.
	 * @param headers
	 *            True, if headers are to be used.
	 * @param format
	 *            The format of the file.
	 */
	public final void process(final File inputFile, final File outputFile,
			final boolean headers, final CSVFormat format) {
		setInputFilename(inputFile);
		setExpectInputHeaders(headers);
		setInputFormat(format);

		readInputFile();
		sortData();
		writeOutputFile(outputFile);
	}

	/**
	 * Read the input file.
	 */
	private void readInputFile() {
		resetStatus();

		final ReadCSV csv = new ReadCSV(getInputFilename().toString(),
				isExpectInputHeaders(), getFormat());
		while (csv.next() && !shouldStop()) {
			updateStatus("Reading input file");
			final LoadedRow row = new LoadedRow(csv);
			this.data.add(row);
		}

		setColumnCount(csv.getColumnCount());

		if (isExpectInputHeaders()) {
			setInputHeadings(new String[csv.getColumnNames().size()]);
			for (int i = 0; i < csv.getColumnNames().size(); i++) {
				getInputHeadings()[i] = csv.getColumnNames().get(i);
			}
		}

		csv.close();
	}

	/**
	 * Sort the loaded data.
	 */
	private void sortData() {
		final Comparator<LoadedRow> comp = new RowComparator(this);
		Collections.sort(this.data, comp);
	}

	/**
	 * Write the sorted output file.
	 * 
	 * @param outputFile
	 *            The name of the output file.
	 */
	private void writeOutputFile(final File outputFile) {
		final PrintWriter tw = prepareOutputFile(outputFile);
		final boolean[] nonNumeric = new boolean[getColumnCount()];
		boolean first = true;

		resetStatus();

		// write the file
		for (final LoadedRow row : this.data) {
			updateStatus("Writing output");
			// for the first row, determine types
			if (first) {
				for (int i = 0; i < getColumnCount(); i++) {
					try {
						final String str = row.getData()[i];
						Double.parseDouble(str);
						nonNumeric[i] = false;
					} catch (final Exception ex) {
						nonNumeric[i] = true;
					}
				}
				first = false;
			}

			// write the row
			final StringBuilder line = new StringBuilder();

			for (int i = 0; i < getColumnCount(); i++) {
				if (i > 0) {
					line.append(",");
				}

				if (nonNumeric[i]) {
					line.append("\"");
					line.append(row.getData()[i]);
					line.append("\"");
				} else {
					line.append(row.getData()[i]);
				}
			}

			tw.println(line.toString());
		}

		reportDone("Writing output");

		// close the file

		tw.close();
	}

}
