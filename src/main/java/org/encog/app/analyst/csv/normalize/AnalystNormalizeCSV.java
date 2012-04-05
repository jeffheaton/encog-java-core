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
package org.encog.app.analyst.csv.normalize;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.EncogError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.csv.TimeSeriesUtil;
import org.encog.app.analyst.csv.basic.BasicFile;
import org.encog.app.analyst.missing.HandleMissingValues;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.util.CSVHeaders;
import org.encog.app.quant.QuantError;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.csv.ReadCSV;
import org.encog.util.logging.EncogLogging;

/**
 * Normalize, or denormalize, a CSV file.
 */
public class AnalystNormalizeCSV extends BasicFile {

	/**
	 * Extract fields from a file into a numeric array for machine learning.
	 * @param analyst The analyst to use.
	 * @param headers The headers for the input data.
	 * @param csv The CSV that holds the input data.
	 * @param outputLength The length of the returned array.
	 * @param skipOutput True if the output should be skipped.
	 * @return The encoded data.
	 */
	public static final double[] extractFields(final EncogAnalyst analyst,
			final CSVHeaders headers, final ReadCSV csv,
			final int outputLength, final boolean skipOutput) {
		final double[] output = new double[outputLength];
		int outputIndex = 0;
		for (final AnalystField stat : analyst.getScript().getNormalize()
				.getNormalizedFields()) {
				
			stat.init();

			if (stat.getAction() == NormalizationAction.Ignore) {
				continue;
			}
			
			if (stat.isOutput() && skipOutput) {
				continue;
			}

			int index = headers.find(stat.getName());
			final String str = csv.get(index).trim();
			
			// is this an unknown value?
			if( str.equals("?") || str.length()==0 ) {				
				HandleMissingValues handler = analyst.getScript().getNormalize().getMissingValues();
				double[] d = handler.handleMissing(analyst ,stat);
				
				// should we skip the entire row
				if( d==null ) {
					return null;
				}
				
				// copy the returned values in place of the missing values
				for(int i=0;i<d.length;i++) {
					output[outputIndex++] = d[i];
				}
			} else {
			// known value
				if (stat.getAction() == NormalizationAction.Normalize) {
					double d = csv.getFormat().parse(str);
					d = stat.normalize(d);
					output[outputIndex++] = d;
				} else if (stat.getAction() == NormalizationAction.PassThrough) {
					double d = csv.getFormat().parse(str);
					output[outputIndex++] = d;
				} else {
					final double[] d = stat.encode(str);
					for (final double element : d) {
						output[outputIndex++] = element;
					}
				}
			}
		}

		return output;

	}

	/**
	 * The analyst to use.
	 */
	private EncogAnalyst analyst;
	
	/**
	 * Used to process time series.
	 */
	private TimeSeriesUtil series;

	/**
	 * THe headers.
	 */
	private CSVHeaders analystHeaders;

	/**
	 * Analyze the file.
	 * @param inputFilename The input file.
	 * @param expectInputHeaders True, if input headers are present.
	 * @param inputFormat The format.
	 * @param theAnalyst The analyst to use.
	 */
	public final void analyze(final File inputFilename,
			final boolean expectInputHeaders, final CSVFormat inputFormat,
			final EncogAnalyst theAnalyst) {
		this.setInputFilename(inputFilename);
		this.setInputFormat(inputFormat);
		this.setExpectInputHeaders(expectInputHeaders);
		this.analyst = theAnalyst;
		this.setAnalyzed(true);

		this.analystHeaders = new CSVHeaders(inputFilename, expectInputHeaders,
				inputFormat);

		for (final AnalystField field : analyst.getScript().getNormalize()
				.getNormalizedFields()) {
			field.init();
		}

		this.series = new TimeSeriesUtil(analyst,true,
				this.analystHeaders.getHeaders());
	}

	/**
	 * Normalize the input file. Write to the specified file.
	 * 
	 * @param file
	 *            The file to write to.
	 */
	public final void normalize(final File file) {
		if (this.analyst == null) {
			throw new EncogError(
					"Can't normalize yet, file has not been analyzed.");
		}

		ReadCSV csv = null;
		PrintWriter tw = null;

		try {
			csv = new ReadCSV(getInputFilename().toString(),
					isExpectInputHeaders(), getFormat());

			tw = new PrintWriter(new FileWriter(file));

			// write headers, if needed
			if (isProduceOutputHeaders()) {
				writeHeaders(tw);
			}

			resetStatus();
			final int outputLength = this.analyst.determineTotalColumns();

			// write file contents
			while (csv.next() && !shouldStop()) {
				updateStatus(false);

				double[] output = AnalystNormalizeCSV.extractFields(
						this.analyst, this.analystHeaders, csv, outputLength,
						false);

				if (this.series.getTotalDepth() > 1) {
					output = this.series.process(output);
				}

				if (output != null) {
					final StringBuilder line = new StringBuilder();
					NumberList.toList(getFormat(), line, output);
					tw.println(line);
				}
			}
		} catch (final IOException e) {
			throw new QuantError(e);
		} finally {
			reportDone(false);
			if (csv != null) {
				try {
					csv.close();
				} catch (final Exception ex) {
					EncogLogging.log(ex);
				}
			}

			if (tw != null) {
				try {
					tw.close();
				} catch (final Exception ex) {
					EncogLogging.log(ex);
				}
			}
		}
	}

	/**
	 * Set the source file. This is useful if you want to use pre-existing stats
	 * to normalize something and skip the analyze step.
	 * 
	 * @param file
	 *            The file to use.
	 * @param headers
	 *            True, if headers are to be expected.
	 * @param format
	 *            The format of the CSV file.
	 */
	public final void setSourceFile(final File file, final boolean headers,
			final CSVFormat format) {
		setInputFilename(file);
		setExpectInputHeaders(headers);
		setInputFormat(format);
	}

	/**
	 * Write the headers.
	 * 
	 * @param tw
	 *            The output stream.
	 */
	private void writeHeaders(final PrintWriter tw) {
		final StringBuilder line = new StringBuilder();
		for (final AnalystField stat : this.analyst.getScript().getNormalize()
				.getNormalizedFields()) {
			final int needed = stat.getColumnsNeeded();

			for (int i = 0; i < needed; i++) {
				BasicFile.appendSeparator(line, getFormat());
				line.append('\"');
				line.append(CSVHeaders.tagColumn(stat.getName(), i,
						stat.getTimeSlice(), needed > 1));
				line.append('\"');
			}
		}
		tw.println(line.toString());
	}

}
