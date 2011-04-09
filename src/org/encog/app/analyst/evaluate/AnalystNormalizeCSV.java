package org.encog.app.analyst.evaluate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.encog.EncogError;
import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.util.CSVHeaders;
import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.BasicFile;
import org.encog.app.quant.normalize.NormalizationAction;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.csv.ReadCSV;

/**
 * Normalize, or denormalize, a CSV file.
 * 
 */
public class AnalystNormalizeCSV extends BasicFile {

	private EncogAnalyst analyst;
	private Map<String, Integer> columnMapping = new HashMap<String, Integer>();
	private TimeSeriesUtil series;

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
	public void setSourceFile(File file, boolean headers, CSVFormat format) {
		this.setInputFilename(file);
		this.setExpectInputHeaders(headers);
		this.setInputFormat(format);
	}

	/**
	 * Write the headers.
	 * 
	 * @param tw
	 *            The output stream.
	 */
	private void writeHeaders(PrintWriter tw) {
		StringBuilder line = new StringBuilder();
		for (AnalystField stat : this.analyst.getScript().getNormalize()
				.getNormalizedFields()) {
			int needed = stat.getColumnsNeeded();

			for (int i = 0; i < needed; i++) {
				BasicFile.appendSeparator(line, this.getInputFormat());
				line.append('\"');
				line.append(CSVHeaders.tagColumn(stat.getName(), i,
						stat.getTimeSlice(), needed > 1));
				line.append('\"');
			}
		}
		tw.println(line.toString());
	}

	/**
	 * Normalize the input file. Write to the specified file.
	 * 
	 * @param file
	 *            The file to write to.
	 */
	public void normalize(File file) {
		if (this.analyst == null)
			throw new EncogError(
					"Can't normalize yet, file has not been analyzed.");

		ReadCSV csv = null;
		PrintWriter tw = null;

		try {
			csv = new ReadCSV(this.getInputFilename().toString(),
					this.isExpectInputHeaders(), this.getInputFormat());

			tw = new PrintWriter(new FileWriter(file));

			// write headers, if needed
			if (this.isProduceOutputHeaders()) {
				writeHeaders(tw);
			}

			resetStatus();
			double[] output = new double[this.analyst.determineUniqueColumns()];

			// write file contents
			while (csv.next() && !this.shouldStop()) {
				updateStatus(false);

				int outputIndex = 0;
				for (AnalystField stat : this.analyst.getScript()
						.getNormalize().getNormalizedFields()) {
					if (this.columnMapping.containsValue(stat.getName()
							.toLowerCase())) {
						throw new AnalystError("Can't find column: "
								+ stat.getName().toLowerCase());
					}
					int index = this.columnMapping.get(stat.getName()
							.toLowerCase());
					String str = csv.get(index++);

					if (stat.getAction() == NormalizationAction.Normalize) {
						double d = this.getInputFormat().parse(str);
						d = stat.normalize(d);
						output[outputIndex++] = d;
					} else {
						double[] d = stat.encode(str);
						for (int i = 0; i < d.length; i++) {
							output[outputIndex++] = d[i];
						}
					}
				}

				double[] out = output;
				if (this.series.getTotalDepth() > 0) {
					out = this.series.process(output);
				}

				if (out != null) {
					StringBuilder line = new StringBuilder();
					NumberList.toList(this.getOutputFormat(), line, out);
					tw.println(line);
				}
			}
		} catch (IOException e) {
			throw new QuantError(e);
		} finally {
			reportDone(false);
			if (csv != null) {
				try {
					csv.close();
				} catch (Exception ex) {
				}
			}

			if (tw != null) {
				try {
					tw.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	public void analyze(File inputFilename, boolean expectInputHeaders,
			CSVFormat inputFormat, EncogAnalyst analyst) {
		this.inputFilename = inputFilename;
		this.inputFormat = inputFormat;
		this.expectInputHeaders = expectInputHeaders;
		this.analyst = analyst;
		this.analyzed = true;

		CSVHeaders headers = new CSVHeaders(inputFilename, expectInputHeaders,
				inputFormat);

		int index = 0;
		for (String str : headers.getHeaders()) {
			this.columnMapping.put(str.toLowerCase(), index++);
		}

		for (AnalystField field : analyst.getScript().getNormalize()
				.getNormalizedFields()) {
			field.init();
		}

		this.series = new TimeSeriesUtil(analyst, headers.getHeaders());
	}

}
