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
package org.encog.app.analyst.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.csv.basic.BasicFile;
import org.encog.app.analyst.csv.basic.LoadedRow;
import org.encog.app.analyst.csv.normalize.AnalystNormalizeCSV;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.util.CSVHeaders;
import org.encog.app.quant.QuantError;
import org.encog.ml.MLClassification;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.bayesian.BayesianNetwork;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.util.arrayutil.ClassItem;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Used by the analyst to evaluate a CSV file.
 * 
 */
public class AnalystEvaluateCSV extends BasicFile {

	/**
	 * The analyst to use.
	 */
	private EncogAnalyst analyst;
	
	/**
	 * The number of columns in the file.
	 */
	private int fileColumns;
	
	/**
	 * The number of output columns.
	 */
	private int outputColumns;
	
	/**
	 * Used to handle time series.
	 */
	private TimeSeriesUtil series;
	
	/**
	 * The headers.
	 */
	private CSVHeaders analystHeaders;

	/**
	 *  Analyze the data. This counts the records and prepares the data to be
	 * processed.
	 * @param theAnalyst The analyst to use.
	 * @param inputFile The input file.
	 * @param headers True if headers are present.
	 * @param format The format.
	 */
	public final void analyze(final EncogAnalyst theAnalyst, 
			final File inputFile,
			final boolean headers, final CSVFormat format) {
		this.setInputFilename(inputFile);
		setExpectInputHeaders(headers);
		setInputFormat(format);

		setAnalyzed(true);
		this.analyst = theAnalyst;

		performBasicCounts();
		this.fileColumns = this.getInputHeadings().length;
		this.outputColumns = this.analyst.determineOutputFieldCount();

		this.analystHeaders = new CSVHeaders(this.getInputHeadings());
		this.series = new TimeSeriesUtil(analyst,false,
				this.analystHeaders.getHeaders());

	}

	/**
	 * Prepare the output file, write headers if needed.
	 * @param outputFile The output file.
	 * @param input The input count.
	 * @param output The output count.
	 * @return The file to write to.
	 */
	private PrintWriter prepareOutputFile(MLMethod method, final File outputFile,
			final int input, final int output) {
		try {
			final PrintWriter tw = new PrintWriter(new FileWriter(outputFile));

			// write headers, if needed
			if (isProduceOutputHeaders()) {
				final StringBuilder line = new StringBuilder();

				// handle provided fields, not all may be used, but all should
				// be displayed
				for (final String heading : this.getInputHeadings()) {
					BasicFile.appendSeparator(line, getFormat());
					line.append("\"");
					line.append(heading);
					line.append("\"");
				}

				// now add the output fields that will be generated
				for (final AnalystField field : this.analyst.getScript()
						.getNormalize().getNormalizedFields()) {
					if (field.isOutput() && !field.isIgnored()) {
						BasicFile.appendSeparator(line, getFormat());
						line.append("\"Output:");
						line.append(CSVHeaders.tagColumn(field.getName(), 0,
								field.getTimeSlice(), false));
						line.append("\"");
					}
				}
				
				// add in Bayesian output column, if needed
				String otherOutput = "";
				if( method instanceof BayesianNetwork ) {
					otherOutput = ((BayesianNetwork)method).getClassificationTargetEvent().getLabel();
					BasicFile.appendSeparator(line, getFormat());
					line.append("\"Output:");
					line.append(otherOutput);
					line.append("\"");
				}
				
				
				tw.println(line.toString());
			}

			return tw;

		} catch (final IOException e) {
			throw new QuantError(e);
		}
	}

	/**
	 * Process the file.
	 * @param outputFile The output file.
	 * @param method THe method to use.
	 */
	public final void process(final File outputFile, 
			final MLMethod method) {

		final ReadCSV csv = new ReadCSV(getInputFilename().toString(),
				isExpectInputHeaders(), getFormat());

		MLData output = null;
		
		for (final AnalystField field : analyst.getScript().getNormalize()
				.getNormalizedFields()) {
			field.init();
		}

		final int outputLength = this.analyst.determineTotalInputFieldCount();

		final PrintWriter tw = this.prepareOutputFile(method, outputFile, this.analyst
				.getScript().getNormalize().countActiveFields() - 1, 1);

		resetStatus();
		while (csv.next()) {
			updateStatus(false);
			final LoadedRow row = new LoadedRow(csv, this.outputColumns);

			double[] inputArray = AnalystNormalizeCSV.extractFields(analyst,
					this.analystHeaders, csv, outputLength, true);
			if (this.series.getTotalDepth() > 1) {
				inputArray = this.series.process(inputArray);
			}

			if (inputArray != null) {
				final MLData input = new BasicMLData(inputArray);

				// evaluation data
				if ((method instanceof MLClassification)
						&& !(method instanceof MLRegression)) {
					// classification only?
					output = new BasicMLData(1);
					output.setData(0,
							((MLClassification) method).classify(input));
				} else {
					// regression
					output = ((MLRegression) method).compute(input);
				}

				// skip file data
				int index = this.fileColumns;
				int outputIndex = 0;
				
				String otherOutput = "";
				if( method instanceof BayesianNetwork ) {
					otherOutput = ((BayesianNetwork)method).getClassificationTargetEvent().getLabel();
				}

				// display output
				for (final AnalystField field : analyst.getScript()
						.getNormalize().getNormalizedFields()) {
					if (this.analystHeaders.find(field.getName()) != -1) {

						if (field.isOutput() || field.getName().equals(otherOutput)) {
							if (field.isClassify()) {
								// classification
								final ClassItem cls = field.determineClass(
										outputIndex, output.getData());
								outputIndex += field.getColumnsNeeded();
								if (cls == null) {
									row.getData()[index++] = "?Unknown?";
								} else {
									row.getData()[index++] = cls.getName();
								}
							} else {
								// regression
								double n = output.getData(outputIndex++);
								n = field.deNormalize(n);
								row.getData()[index++] = getFormat()
										.format(n, getPrecision());
							}
						}
					}
				}
			}

			writeRow(tw, row);
		}
		reportDone(false);
		tw.close();
		csv.close();
	}
}
