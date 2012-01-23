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
import java.io.PrintWriter;

import org.encog.EncogError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.csv.TimeSeriesUtil;
import org.encog.app.analyst.csv.basic.BasicFile;
import org.encog.app.analyst.missing.HandleMissingValues;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.util.CSVHeaders;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.buffer.BufferedMLDataSet;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.logging.EncogLogging;

/**
 * Normalize, or denormalize, a CSV file.
 */
public class AnalystNormalizeToEGB extends BasicFile {

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
		
		int inputCount = analyst.getScript().getNormalize().calculateInputColumns(); 
		int idealCount = analyst.getScript().getNormalize().calculateOutputColumns();
		
 	   	BasicMLData inputData = new BasicMLData(inputCount);
 	   	BasicMLData idealData = new BasicMLData(idealCount);

		ReadCSV csv = null;
		BufferedMLDataSet buffer = new BufferedMLDataSet(file);
		buffer.beginLoad(inputCount,idealCount);

		try {
			csv = new ReadCSV(getInputFilename().toString(),
					isExpectInputHeaders(), getFormat());

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
				
				// copy the input
				int idx = 0;
				for(int i=0;i<inputData.size();i++) {
					inputData.setData(i, output[idx++]);
				}
				
				for(int i=0;i<idealData.size();i++) {
					idealData.setData(i, output[idx++]);
				}

				if (output != null) {
					buffer.add(inputData,idealData);
				}
			}
		} finally {
			reportDone(false);
			if (csv != null) {
				try {
					csv.close();
				} catch (final Exception ex) {
					EncogLogging.log(ex);
				}
			}

			if (buffer != null) {
				try {
					buffer.endLoad();
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


}
