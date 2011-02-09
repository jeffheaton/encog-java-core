package org.encog.app.quant.basic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.NullStatusReportable;
import org.encog.app.quant.QuantError;
import org.encog.engine.StatusReportable;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Many of the Encog quant CSV processors are based upon this class. This class
 * is not useful on its own. However, it does form the foundation for most Encog
 * CSV file processing.
 */
public class BasicFile {

	/**
	 * The column headings from the input file.
	 */
	public String[] inputHeadings;

	/**
	 * The desired precision when numbers must be written. Defaults to 10
	 * decimal places.
	 */
	public int precision;

	/**
	 * Most Encog CSV classes must analyze a CSV file before actually processing
	 * it. This property specifies if the file has been analyzed yet.
	 */
	public boolean analyzed;

	/**
	 * The input filename. This is the file being analyzed/processed.
	 */
	public String inputFilename;

	/**
	 * True, if input headers should be expected.
	 */
	public boolean expectInputHeaders;

	/**
	 * The format of the input file.
	 */
	public CSVFormat inputFormat;

	/**
	 * The number of columns in the input file.
	 */
	public int columnCount;

	/**
	 * Allows status to be reported. Defaults to no status reported.
	 */
	public StatusReportable report;

	/**
	 * The number of records to process before status is updated. Defaults to
	 * 10k.
	 */
	public int reportInterval;

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
	 * Construct the object, and set the defaults.
	 */
	public BasicFile() {
		this.report = new NullStatusReportable();
		this.reportInterval = 10000;
		resetStatus();
	}

	/**
	 * Prepare the output file, write headers if needed.
	 * 
	 * @param outputFile
	 *            The name of the output file.
	 * @return The output stream for the text file.
	 */
	public PrintWriter prepareOutputFile(String outputFile) {
		try {
			PrintWriter tw = new PrintWriter(new FileWriter(outputFile));

			// write headers, if needed
			if (expectInputHeaders) {
				int index = 0;
				StringBuilder line = new StringBuilder();
				for (String str : this.inputHeadings) {
					if (line.length() > 0) {
						line.append(",");
					}
					line.append("\"");
					line.append(str);
					line.append("\"");
					index++;
				}
				tw.println(line.toString());
			}

			return tw;

		} catch (IOException e) {
			throw new QuantError(e);
		}
	}

	/**
	 * @return Get the record count. File must have been analyzed first to read
	 *         the record count.
	 */
	public int getRecordCount() {
		if (!analyzed) {
			throw new QuantError("Must analyze file first.");
		}
		return this.recordCount;

	}

	/**
	 * Set the record count.
	 * 
	 * @param v
	 *            The record count.
	 */
	public void setRecordCount(int v) {
		this.recordCount = v;
	}

	/**
	 * Validate that the file has been analyzed. Throw an error, if it has not.
	 */
	public void validateAnalyzed() {
		if (!analyzed) {
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
	public void WriteRow(PrintWriter tw, LoadedRow row) {
		StringBuilder line = new StringBuilder();

		for (int i = 0; i < this.columnCount; i++) {
			if (i > 0) {
				line.append(",");
			}

			/*
			 * if (nonNumeric[i]) { line.Append("\""); line.Append(row.Data[i]);
			 * line.Append("\""); } else
			 */
			{
				line.append(row.getData()[i]);
			}
		}

		tw.println(line.toString());
	}

	/**
	 * Perform a basic analyze of the file. This method is used mostly
	 * internally.
	 */
	public void performBasicCounts() {
		resetStatus();
		int recordCount = 0;
		ReadCSV csv = new ReadCSV(this.inputFilename, this.expectInputHeaders,
				this.inputFormat);
		while (csv.next()) {
			updateStatus(true);
			recordCount++;
		}
		this.recordCount = recordCount;
		this.columnCount = csv.getColumnCount();

		readHeaders(csv);
		csv.close();
		reportDone(true);
	}

	/**
	 * Read the headers from a CSV file. Used mostly internally.
	 * 
	 * @param csv
	 *            The CSV file to read from.
	 */
	public void readHeaders(ReadCSV csv) {
		if (this.expectInputHeaders) {
			this.inputHeadings = new String[csv.getColumnNames().size()];
			for (int i = 0; i < csv.getColumnNames().size(); i++) {
				this.inputHeadings[i] = csv.getColumnNames().get(i);
			}
		}
	}

	/**
	 * Reset the reporting stats. Used internally.
	 */
	public void resetStatus() {
		this.lastUpdate = 0;
		this.currentRecord = 0;
	}

	/**
	 * Update the status. Used internally.
	 * 
	 * @param isAnalyzing
	 *            True if we are in the process of analyzing.
	 */
	public void updateStatus(boolean isAnalyzing) {
		if (isAnalyzing) {
			updateStatus("Analyzing");
		} else {
			updateStatus("Processing");
		}
	}

	/**
	 * Report that we are done. Used internally.
	 * 
	 * @param isAnalyzing
	 *            True if we are analyzing.
	 */
	public void reportDone(boolean isAnalyzing) {
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
	public void reportDone(String task) {
		this.report.report(this.recordCount, this.recordCount, task);
	}

	/**
	 * Report the current status.
	 * 
	 * @param task
	 *            The string to report.
	 */
	public void updateStatus(String task) {
		boolean shouldDisplay = false;

		if (this.currentRecord == 0) {
			shouldDisplay = true;
		}

		this.currentRecord++;
		this.lastUpdate++;

		if (lastUpdate > this.reportInterval) {
			lastUpdate = 0;
			shouldDisplay = true;
		}

		if (shouldDisplay) {
			this.report.report(this.recordCount, this.currentRecord, task);
		}
	}

	/**
	 * @return The input headings.
	 */
	public String[] getInputHeadings() {
		return inputHeadings;
	}

	/**
	 * Set the input headings.
	 * 
	 * @param inputHeadings
	 *            The new input headings.
	 */
	public void setInputHeadings(String[] inputHeadings) {
		this.inputHeadings = inputHeadings;
	}

	/**
	 * @return The precision to use.
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * Set the precision to use.
	 * 
	 * @param precision
	 *            The precision to use.
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	/**
	 * @return Has the file been analyzed.
	 */
	public boolean isAnalyzed() {
		return analyzed;
	}

	/**
	 * Set to true, if the file has been analyzed.
	 * 
	 * @param analyzed
	 *            True, if the file has been analyzed.
	 */
	public void setAnalyzed(boolean analyzed) {
		this.analyzed = analyzed;
	}

	/**
	 * @return The input filename.
	 */
	public String getInputFilename() {
		return inputFilename;
	}

	/**
	 * Set the input filename.
	 * 
	 * @param inputFilename
	 *            The input filename.
	 */
	public void setInputFilename(String inputFilename) {
		this.inputFilename = inputFilename;
	}

	/**
	 * @return True if we are expecting input headers.
	 */
	public boolean isExpectInputHeaders() {
		return expectInputHeaders;
	}

	/**
	 * Set the flag to determine if we are expecting input headers.
	 * 
	 * @param expectInputHeaders
	 */
	public void setExpectInputHeaders(boolean expectInputHeaders) {
		this.expectInputHeaders = expectInputHeaders;
	}

	/**
	 * @return THe input format.
	 */
	public CSVFormat getInputFormat() {
		return inputFormat;
	}

	/**
	 * Set the input format.
	 * 
	 * @param inputFormat
	 *            The new input format.
	 */
	public void setInputFormat(CSVFormat inputFormat) {
		this.inputFormat = inputFormat;
	}

	/**
	 * @return The column count.
	 */
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * Set the column count.
	 * 
	 * @param columnCount
	 *            The new column count.
	 */
	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	/**
	 * @return The status reporting object.
	 */
	public StatusReportable getReport() {
		return report;
	}

	/**
	 * Set the status reporting object.
	 * 
	 * @param report
	 *            The status reporting object.
	 */
	public void setReport(StatusReportable report) {
		this.report = report;
	}

	/**
	 * @return The reporting interval, an update will be sent for every block of
	 *         rows that matches the size of this property.
	 */
	public int getReportInterval() {
		return reportInterval;
	}

	/**
	 * Set the reporting interval.
	 * @param reportInterval The new reporting interval.
	 */
	public void setReportInterval(int reportInterval) {
		this.reportInterval = reportInterval;
	}

}
