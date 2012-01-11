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
package org.encog.app.analyst.csv.basic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.Encog;
import org.encog.NullStatusReportable;
import org.encog.StatusReportable;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.DataField;
import org.encog.app.quant.QuantError;
import org.encog.app.quant.QuantTask;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Many of the Encog quant CSV processors are based upon this class. This class
 * is not useful on its own. However, it does form the foundation for most Encog
 * CSV file processing.
 */
public class BasicFile implements QuantTask {

	/**
	 * The default report interval.
	 */
	public static final int REPORT_INTERVAL = 10000;

	/**
	 * Append a separator. The separator will only be appended if the line is
	 * not empty.  This is used to build comma(or other) separated lists.
	 * 
	 * @param line
	 *            The line to append to.
	 * @param format
	 * 			The format to use.
	 */
	public static void appendSeparator(final StringBuilder line,
			final CSVFormat format) {
		if ((line.length() > 0)
				&& !line.toString().endsWith(format.getSeparator() + "")) {
			line.append(format.getSeparator());
		}
	}

	/**
	 * The column headings from the input file.
	 */
	private String[] inputHeadings;

	/**
	 * The desired precision when numbers must be written. Defaults to 10
	 * decimal places.
	 */
	private int precision;

	/**
	 * Most Encog CSV classes must analyze a CSV file before actually processing
	 * it. This property specifies if the file has been analyzed yet.
	 */
	private boolean analyzed;

	/**
	 * The input filename. This is the file being analyzed/processed.
	 */
	private File inputFilename;

	/**
	 * True, if input headers should be expected.
	 */
	private boolean expectInputHeaders;

	/**
	 * The format of the input file.
	 */
	private CSVFormat format;

	/**
	 * The number of columns in the input file.
	 */
	private int columnCount;

	/**
	 * Allows status to be reported. Defaults to no status reported.
	 */
	private StatusReportable report;

	/**
	 * The number of records to process before status is updated. Defaults to
	 * 10k.
	 */
	private int reportInterval;

	/**
	 * The number of records to process. This is determined when the file is
	 * analyzed.
	 */
	private int recordCount;

	/**
	 * The last time status was updated.
	 */
	private int lastUpdate;

	/**
	 * The current record.
	 */
	private int currentRecord;

	/**
	 * Should output headers be produced?
	 */
	private boolean produceOutputHeaders;

	/**
	 * True, if the process should stop.
	 */
	private boolean cancel;


	/**
	 * The Encog script to use.
	 */
	private AnalystScript script;

	/**
	 * Construct the object, and set the defaults.
	 */
	public BasicFile() {
		this.precision = Encog.DEFAULT_PRECISION;
		this.report = new NullStatusReportable();
		this.reportInterval = REPORT_INTERVAL;
		this.produceOutputHeaders = true;
		resetStatus();
	}

	/**
	 * @return The column count.
	 */
	public final int getColumnCount() {
		return this.columnCount;
	}

	/**
	 * @return The input filename.
	 */
	public final File getInputFilename() {
		return this.inputFilename;
	}

	/**
	 * @return THe input format.
	 */
	public final CSVFormat getFormat() {
		return this.format;
	}

	/**
	 * @return The input headings.
	 */
	public final String[] getInputHeadings() {
		return this.inputHeadings;
	}

	/**
	 * @return The precision to use.
	 */
	public final int getPrecision() {
		return this.precision;
	}

	/**
	 * @return Get the record count. File must have been analyzed first to read
	 *         the record count.
	 */
	public final int getRecordCount() {
		if (!this.analyzed) {
			throw new QuantError("Must analyze file first.");
		}
		return this.recordCount;

	}

	/**
	 * @return The status reporting object.
	 */
	public final StatusReportable getReport() {
		return this.report;
	}

	/**
	 * @return The reporting interval, an update will be sent for every block of
	 *         rows that matches the size of this property.
	 */
	public final int getReportInterval() {
		return this.reportInterval;
	}

	/**
	 * @return Has the file been analyzed.
	 */
	public final boolean isAnalyzed() {
		return this.analyzed;
	}

	/**
	 * @return True if we are expecting input headers.
	 */
	public final boolean isExpectInputHeaders() {
		return this.expectInputHeaders;
	}

	/**
	 * @return the produceOutputHeaders
	 */
	public final boolean isProduceOutputHeaders() {
		return this.produceOutputHeaders;
	}

	/**
	 * Perform a basic analyze of the file. This method is used mostly
	 * internally.
	 */
	public final void performBasicCounts() {

		resetStatus();
		int rc = 0;
		final ReadCSV csv = new ReadCSV(this.inputFilename.toString(),
				this.expectInputHeaders, this.format);
		while (csv.next() && !this.cancel) {
			updateStatus(true);
			rc++;
		}
		this.recordCount = rc;
		this.columnCount = csv.getColumnCount();

		readHeaders(csv);
		csv.close();
		reportDone(true);
	}

	/**
	 * Prepare the output file, write headers if needed.
	 * 
	 * @param outputFile
	 *            The name of the output file.
	 * @return The output stream for the text file.
	 */
	public final PrintWriter prepareOutputFile(final File outputFile) {
		try {
			final PrintWriter tw = new PrintWriter(new FileWriter(outputFile));

			// write headers, if needed
			if (this.produceOutputHeaders) {
				int index = 0;
				final StringBuilder line = new StringBuilder();

				if (this.inputHeadings != null) {
					for (final String str : this.inputHeadings) {
						if (line.length() > 0) {
							line.append(this.format.getSeparator());
						}
						line.append("\"");
						line.append(str);
						line.append("\"");
						index++;
					}
				} else {
					for (int i = 0; i < this.columnCount; i++) {
						line.append("\"field:");
						line.append(i + 1);
						line.append("\"");
					}
				}
				tw.println(line.toString());
			}

			return tw;

		} catch (final IOException e) {
			throw new QuantError(e);
		}
	}

	/**
	 * Read the headers from a CSV file. Used mostly internally.
	 * 
	 * @param csv
	 *            The CSV file to read from.
	 */
	public final void readHeaders(final ReadCSV csv) {
		if (this.expectInputHeaders) {
			this.inputHeadings = new String[csv.getColumnNames().size()];
			for (int i = 0; i < csv.getColumnNames().size(); i++) {
				this.inputHeadings[i] = csv.getColumnNames().get(i);
			}
		} else {
			this.inputHeadings = new String[csv.getColumnCount()];
			
			int i = 0;
			if (this.getScript() != null) {
				for (DataField field : this.getScript().getFields()) {
					this.inputHeadings[i++] = field.getName();
				}
			}
			
			while (i < csv.getColumnCount()) {
				this.inputHeadings[i] = "field:" + i;
				i++;
			}
		}
	}

	/**
	 * Report that we are done. Used internally.
	 * 
	 * @param isAnalyzing
	 *            True if we are analyzing.
	 */
	public final void reportDone(final boolean isAnalyzing) {
		if (isAnalyzing) {
			this.report.report(this.recordCount, this.recordCount,
					"Done analyzing");
		} else {
			this.report.report(this.recordCount, this.recordCount,
					"Done processing");
		}
	}

	/**
	 * Report that we are done. Used internally.
	 * 
	 * @param task
	 *            The message.
	 */
	public final void reportDone(final String task) {
		this.report.report(this.recordCount, this.recordCount, task);
	}

	/**
	 * Request a stop.
	 */
	@Override
	public final void requestStop() {
		this.cancel = true;
	}

	/**
	 * Reset the reporting stats. Used internally.
	 */
	public final void resetStatus() {
		this.lastUpdate = 0;
		this.currentRecord = 0;
	}

	/**
	 * Set to true, if the file has been analyzed.
	 * 
	 * @param theAnalyzed
	 *            True, if the file has been analyzed.
	 */
	public final void setAnalyzed(final boolean theAnalyzed) {
		this.analyzed = theAnalyzed;
	}

	/**
	 * Set the column count.
	 * 
	 * @param theColumnCount
	 *            The new column count.
	 */
	public final void setColumnCount(final int theColumnCount) {
		this.columnCount = theColumnCount;
	}

	/**
	 * Set the flag to determine if we are expecting input headers.
	 * 
	 * @param theExpectInputHeaders Are input headers expected?
	 */
	public final void setExpectInputHeaders(
			final boolean theExpectInputHeaders) {
		this.expectInputHeaders = theExpectInputHeaders;
	}

	/**
	 * Set the input filename.
	 * 
	 * @param theInputFilename
	 *            The input filename.
	 */
	public final void setInputFilename(final File theInputFilename) {
		this.inputFilename = theInputFilename;
	}

	/**
	 * Set the input format.
	 * 
	 * @param theInputFormat
	 *            The new inputFormat format.
	 */
	public final void setInputFormat(final CSVFormat theInputFormat) {
		this.format = theInputFormat;
	}

	/**
	 * Set the input headings.
	 * 
	 * @param theInputHeadings
	 *            The new input headings.
	 */
	public final void setInputHeadings(final String[] theInputHeadings) {
		this.inputHeadings = theInputHeadings;
	}

	/**
	 * Set the precision to use.
	 * 
	 * @param thePrecision
	 *            The precision to use.
	 */
	public final void setPrecision(final int thePrecision) {
		this.precision = thePrecision;
	}

	/**
	 * @param theProduceOutputHeaders
	 *            the produceOutputHeaders to set
	 */
	public final void setProduceOutputHeaders(
			final boolean theProduceOutputHeaders) {
		this.produceOutputHeaders = theProduceOutputHeaders;
	}

	/**
	 * Set the record count.
	 * 
	 * @param v
	 *            The record count.
	 */
	public final void setRecordCount(final int v) {
		this.recordCount = v;
	}

	/**
	 * Set the status reporting object.
	 * 
	 * @param theReport
	 *            The status reporting object.
	 */
	public final void setReport(final StatusReportable theReport) {
		this.report = theReport;
	}

	/**
	 * Set the reporting interval.
	 * 
	 * @param theReportInterval
	 *            The new reporting interval.
	 */
	public final void setReportInterval(final int theReportInterval) {
		this.reportInterval = theReportInterval;
	}

	/**
	 * @return Should we stop?
	 */
	@Override
	public final boolean shouldStop() {
		return this.cancel;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" inputFilename=");
		result.append(this.inputFilename);
		result.append(", recordCount=");
		result.append(this.recordCount);
		result.append("]");
		return result.toString();
	}

	/**
	 * Update the status. Used internally.
	 * 
	 * @param isAnalyzing
	 *            True if we are in the process of analyzing.
	 */
	public final void updateStatus(final boolean isAnalyzing) {
		if (isAnalyzing) {
			updateStatus("Analyzing");
		} else {
			updateStatus("Processing");
		}
	}

	/**
	 * Report the current status.
	 * 
	 * @param task
	 *            The string to report.
	 */
	public final void updateStatus(final String task) {
		boolean shouldDisplay = false;

		if (this.currentRecord == 0) {
			shouldDisplay = true;
		}

		this.currentRecord++;
		this.lastUpdate++;

		if (this.lastUpdate >= this.reportInterval) {
			this.lastUpdate = 0;
			shouldDisplay = true;
		}

		if (shouldDisplay) {
			this.report.report(this.recordCount, this.currentRecord, task);
		}
	}

	/**
	 * Validate that the file has been analyzed. Throw an error, if it has not.
	 */
	public final void validateAnalyzed() {
		if (!this.analyzed) {
			throw new QuantError("File must be analyzed first.");
		}
	}

	/**
	 * Write a row to the output file.
	 * 
	 * @param tw
	 *            The output stream.
	 * @param row
	 *            The row to write out.
	 */
	public final void writeRow(final PrintWriter tw, final LoadedRow row) {
		final StringBuilder line = new StringBuilder();

		for (int i = 0; i < row.getData().length; i++) {
			BasicFile.appendSeparator(line, this.format);
			line.append(row.getData()[i]);
		}

		tw.println(line.toString());
	}

	/**
	 * @return the script
	 */
	public final AnalystScript getScript() {
		return script;
	}

	/**
	 * @param theScript the script to set
	 */
	public final void setScript(final AnalystScript theScript) {
		this.script = theScript;
	}

}
