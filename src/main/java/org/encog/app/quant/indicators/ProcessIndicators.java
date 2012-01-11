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
package org.encog.app.quant.indicators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.app.analyst.csv.basic.BaseCachedColumn;
import org.encog.app.analyst.csv.basic.BasicCachedFile;
import org.encog.app.analyst.csv.basic.FileData;
import org.encog.app.quant.QuantError;
import org.encog.util.csv.ReadCSV;

/**
 * Process indicators and generate output.
 */
public class ProcessIndicators extends BasicCachedFile {

	/**
	 * Allocate storage.
	 */
	private void allocateStorage() {
		for (final BaseCachedColumn column : getColumns()) {
			column.allocate(getRecordCount());
		}
	}

	/**
	 * Calculate the indicators.
	 */
	private void calculateIndicators() {
		for (final BaseCachedColumn column : getColumns()) {
			if (column.isOutput()) {
				if (column instanceof Indicator) {
					final Indicator indicator = (Indicator) column;
					indicator.calculate(getColumnMapping(), getRecordCount());
				}
			}
		}
	}

	/**
	 * @return Get the beginning index.
	 */
	private int getBeginningIndex() {
		int result = 0;

		for (final BaseCachedColumn column : getColumns()) {
			if (column instanceof Indicator) {
				final Indicator ind = (Indicator) column;
				result = Math.max(ind.getBeginningIndex(), result);
			}
		}

		return result;
	}

	/**
	 * @return Get the ending index.
	 */
	private int getEndingIndex() {
		int result = getRecordCount() - 1;

		for (final BaseCachedColumn column : getColumns()) {
			if (column instanceof Indicator) {
				final Indicator ind = (Indicator) column;
				result = Math.min(ind.getEndingIndex(), result);
			}
		}

		return result;
	}

	/**
	 * Process and write the specified output file.
	 * 
	 * @param output
	 *            The output file.
	 */
	public final void process(final File output) {
		validateAnalyzed();

		allocateStorage();
		readFile();
		calculateIndicators();
		writeCSV(output);
	}

	/**
	 * Read the CSV file.
	 */
	private void readFile() {
		ReadCSV csv = null;

		try {
			csv = new ReadCSV(getInputFilename().toString(),
					isExpectInputHeaders(), getFormat());

			resetStatus();
			int row = 0;
			while (csv.next() && !shouldStop()) {
				updateStatus("Reading data");
				for (final BaseCachedColumn column : getColumns()) {
					if (column instanceof FileData) {
						if (column.isInput()) {
							final FileData fd = (FileData) column;
							final String str = csv.get(fd.getIndex());
							final double d = getFormat().parse(str);
							fd.getData()[row] = d;
						}
					}
				}
				row++;
			}
		} finally {
			reportDone("Reading data");
			if (csv != null) {
				csv.close();
			}
		}
	}

	/**
	 * Rename a column.
	 * 
	 * @param index
	 *            The column index.
	 * @param newName
	 *            The new name.
	 */
	public final void renameColumn(final int index, final String newName) {
		getColumnMapping().remove(getColumns().get(index).getName());
		getColumns().get(index).setName(newName);
		getColumnMapping().put(newName, getColumns().get(index));
	}

	/**
	 * Write the CSV.
	 * 
	 * @param filename
	 *            The target filename.
	 */
	private void writeCSV(final File filename) {
		PrintWriter tw = null;

		try {
			resetStatus();
			tw = new PrintWriter(new FileWriter(filename));

			// write the headers
			if (isExpectInputHeaders()) {
				final StringBuilder line = new StringBuilder();

				for (final BaseCachedColumn column : getColumns()) {
					if (column.isOutput()) {
						if (line.length() > 0) {
							line.append(getFormat().getSeparator());
						}
						line.append("\"");
						line.append(column.getName());
						line.append("\"");
					}
				}

				tw.println(line.toString());
			}

			// starting and ending index
			final int beginningIndex = getBeginningIndex();
			final int endingIndex = getEndingIndex();

			// write the file data
			for (int row = beginningIndex; row <= endingIndex; row++) {
				updateStatus("Writing data");
				final StringBuilder line = new StringBuilder();

				for (final BaseCachedColumn column : getColumns()) {
					if (column.isOutput()) {
						if (line.length() > 0) {
							line.append(getFormat().getSeparator());
						}
						final double d = column.getData()[row];
						line.append(getFormat().format(d, getPrecision()));
					}
				}

				tw.println(line.toString());
			}
		} catch (final IOException e) {
			throw (new QuantError(e));
		} finally {
			if (tw != null) {
				tw.close();
			}
		}
	}
}
