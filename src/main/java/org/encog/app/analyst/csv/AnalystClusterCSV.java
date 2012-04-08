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
import org.encog.app.analyst.util.CSVHeaders;
import org.encog.app.quant.QuantError;
import org.encog.ml.MLCluster;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.kmeans.KMeansClustering;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Used by the analyst to cluster a CSV file.
 * 
 */
public class AnalystClusterCSV extends BasicFile {

	/**
	 * The analyst to use.
	 */
	private EncogAnalyst analyst;
	
	/**
	 * The headers.
	 */
	private CSVHeaders analystHeaders;
	
	/**
	 * The training data used to send to KMeans.
	 */
	private BasicMLDataSet data;

	/**
	 * Analyze the data. This counts the records and prepares the data to be
	 * processed.
	 * @param theAnalyst The analyst to use.
	 * @param inputFile The input file to analyze.
	 * @param headers True, if the input file has headers.
	 * @param format The format of the input file.
	 */
	public final void analyze(final EncogAnalyst theAnalyst, 
			final File inputFile,
			final boolean headers, final CSVFormat format) {
		this.setInputFilename(inputFile);
		setExpectInputHeaders(headers);
		setInputFormat(format);

		setAnalyzed(true);
		this.analyst = theAnalyst;
		this.data = new BasicMLDataSet();
		resetStatus();
		int recordCount = 0;

		final int outputLength = this.analyst.determineTotalColumns();
		final ReadCSV csv = new ReadCSV(this.getInputFilename().toString(),
				this.isExpectInputHeaders(), this.getFormat());
		readHeaders(csv);

		this.analystHeaders = new CSVHeaders(this.getInputHeadings());

		while (csv.next() && !shouldStop()) {
			updateStatus(true);
			final double[] inputArray = AnalystNormalizeCSV.extractFields(
					analyst, this.analystHeaders, csv, outputLength, true);
			final MLData input = new BasicMLData(inputArray);
			this.data.add(new BasicMLDataPair(input));

			recordCount++;
		}
		setRecordCount(recordCount);
		this.setColumnCount(csv.getColumnCount());

		readHeaders(csv);
		csv.close();
		reportDone(true);
	}

	/**
	 * Prepare the output file, write headers if needed.
	 * @param outputFile The output file.
	 * @param input The number of input columns.
	 * @param output The number of output columns.
	 * @return The file to be written to.
	 */
	private PrintWriter prepareOutputFile(
			final File outputFile,
			final int input, 
			final int output) {
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

				// now the output fields that will be generated
				BasicFile.appendSeparator(line, getFormat());
				line.append("\"cluster\"");

				tw.println(line.toString());
			}

			return tw;

		} catch (final IOException e) {
			throw new QuantError(e);
		}
	}

	/**
	 * Process the file and cluster.
	 * @param outputFile The output file.
	 * @param clusters The number of clusters.
	 * @param theAnalyst The analyst to use.
	 * @param iterations The number of iterations to use.
	 */
	public final void process(final File outputFile, final int clusters,
			final EncogAnalyst theAnalyst, final int iterations) {

		final PrintWriter tw = this.prepareOutputFile(outputFile, analyst
				.getScript().getNormalize().countActiveFields() - 1, 1);

		resetStatus();

		final KMeansClustering cluster = new KMeansClustering(clusters,
				this.data);
		cluster.iteration(iterations);

		int clusterNum = 0;
		for (final MLCluster cl : cluster.getClusters()) {
			for (final MLData item : cl.getData()) {
				final int clsIndex = item.size();
				final LoadedRow lr = new LoadedRow(this.getFormat(),item.getData(),1);
				lr.getData()[clsIndex] = "" + clusterNum;
				writeRow(tw, lr);
			}
			clusterNum++;
		}

		reportDone(false);
		tw.close();
	}
}
