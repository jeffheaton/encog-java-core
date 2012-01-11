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
package org.encog.app.analyst.csv.filter;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.encog.app.analyst.csv.basic.BasicFile;
import org.encog.app.analyst.csv.basic.LoadedRow;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * This class can be used to remove certain rows from a CSV. You can remove rows
 * where a specific field has a specific value
 * 
 */
public class FilterCSV extends BasicFile {

	/**
	 * The excluded fields.
	 */
	private final List<ExcludedField> excludedFields 
		= new ArrayList<ExcludedField>();

	/**
	 * A count of the filtered rows.
	 */
	private int filteredCount;

	/**
	 * Analyze the file.
	 * 
	 * @param inputFile
	 *            The name of the input file.
	 * @param headers
	 *            True, if headers are expected.
	 * @param format
	 *            The format.
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
	 * Exclude rows where the specified field has the specified value.
	 * 
	 * @param fieldNumber
	 *            The field number.
	 * @param fieldValue
	 *            The field value.
	 */
	public final void exclude(final int fieldNumber, final String fieldValue) {
		this.excludedFields.add(new ExcludedField(fieldNumber, fieldValue));
	}

	/**
	 * @return A list of the fields and their values, that should be excluded.
	 */
	public final List<ExcludedField> getExcluded() {
		return this.excludedFields;
	}

	/**
	 * @return A count of the filtered rows. This is the resulting line count
	 *         for the output CSV.
	 */
	public final int getFilteredRowCount() {
		return this.filteredCount;
	}

	/**
	 * Process the input file.
	 * 
	 * @param outputFile
	 *            The output file to write to.
	 */
	public final void process(final File outputFile) {
		final ReadCSV csv = new ReadCSV(getInputFilename().toString(),
				isExpectInputHeaders(), getFormat());

		final PrintWriter tw = prepareOutputFile(outputFile);
		this.filteredCount = 0;

		resetStatus();
		while (csv.next() && !shouldStop()) {
			updateStatus(false);
			final LoadedRow row = new LoadedRow(csv);
			if (shouldProcess(row)) {
				writeRow(tw, row);
				this.filteredCount++;
			}
		}
		reportDone(false);
		tw.close();
		csv.close();
	}

	/**
	 * Determine if the specified row should be processed, or not.
	 * 
	 * @param row
	 *            The row.
	 * @return True, if the row should be processed.
	 */
	private boolean shouldProcess(final LoadedRow row) {
		for (final ExcludedField field : this.excludedFields) {
			if (row.getData()[field.getFieldNumber()].trim().equals(
					field.getFieldValue().trim())) {
				return false;
			}
		}

		return true;
	}
}
