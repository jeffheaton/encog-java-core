package org.encog.app.analyst.evaluate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.util.CSVHeaders;
import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.BasicFile;
import org.encog.app.quant.basic.LoadedRow;
import org.encog.ml.MLRegression;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Used by the analyst to evaluate a CSV file.
 * 
 */
public class AnalystEvaluateRawCSV extends BasicFile {

	private EncogAnalyst analyst;
	private int inputCount;
	private int outputCount;
	private int idealCount;

	/**
	 * Analyze the data. This counts the records and prepares the data to be
	 * processed.
	 * 
	 * @param inputFile
	 *            The input file to process.
	 * @param headers
	 *            True, if headers are present.
	 * @param format
	 *            The format of the CSV file.
	 */
	public void analyze(EncogAnalyst analyst, File inputFile, boolean headers,
			CSVFormat format) {
		this.inputFilename = inputFile;
		this.setExpectInputHeaders(headers);
		this.setInputFormat(format);
		this.analyst = analyst;

		this.setAnalyzed(true);

		performBasicCounts();

		this.inputCount = this.analyst.determineInputCount();
		this.outputCount = this.analyst.determineOutputCount();
		this.idealCount = this.getInputHeadings().length - inputCount;

		if (this.getInputHeadings().length != inputCount
				&& this.getInputHeadings().length != (inputCount + outputCount)) {
			throw new AnalystError(
					"Invalid number of columns("+this.getInputHeadings().length+"), must match input(" + inputCount
							+ ") count or input+output("
							+ (inputCount + outputCount) + ") count.");
		}

	}

	/**
	 * Prepare the output file, write headers if needed.
	 * 
	 * @param outputFile
	 *            The name of the output file.
	 * @param method
	 * @return The output stream for the text file.
	 */
	public PrintWriter prepareOutputFile(File outputFile) {
		try {
			PrintWriter tw = new PrintWriter(new FileWriter(outputFile));

			// write headers, if needed
			if (this.isProduceOutputHeaders()) {
				StringBuilder line = new StringBuilder();

				// first handle the input fields
				for (AnalystField field : this.analyst.getScript()
						.getNormalize().getNormalizedFields()) {
					if (field.isInput()) {
						field.addRawHeadings(line, null, this.getOutputFormat());
					}
				}

				// now, handle any ideal fields
				if (this.idealCount > 0) {
					for (AnalystField field : this.analyst.getScript()
							.getNormalize().getNormalizedFields()) {
						if (field.isOutput()) {
							field.addRawHeadings(line, "ideal:",
									this.getOutputFormat());
						}
					}
				}

				// now, handle the output fields
				for (AnalystField field : this.analyst.getScript()
						.getNormalize().getNormalizedFields()) {
					if (field.isOutput()) {
						field.addRawHeadings(line, "output:",
								this.getOutputFormat());
					}
				}

				tw.println(line.toString());
			}

			return tw;

		} catch (IOException e) {
			throw new QuantError(e);
		}
	}

	public void process(File outputFile, EncogAnalyst analyst,
			MLRegression method, String targetFieldName) {

		ReadCSV csv = new ReadCSV(this.getInputFilename().toString(),
				this.isExpectInputHeaders(), this.getInputFormat());

		if (method.getInputCount() != this.inputCount) {
			throw new AnalystError("This machine learning method has "
					+ method.getInputCount()
					+ " inputs, however, the data has " + this.inputCount
					+ " inputs.");
		}

		NeuralData output = null;
		NeuralData input = new BasicNeuralData(method.getInputCount());

		PrintWriter tw = this.prepareOutputFile(outputFile);

		resetStatus();
		while (csv.next()) {
			updateStatus(false);
			LoadedRow row = new LoadedRow(csv, this.idealCount);

			int dataIndex = 0;
			// load the input data
			for (int i = 0; i < this.inputCount; i++) {
				String str = row.getData()[i];
				double d = this.getInputFormat().parse(str);
				input.setData(i, d);
				dataIndex++;
			}

			// do we need to skip the ideal values?
			dataIndex += this.idealCount;

			// compute the result
			output = method.compute(input);

			// display the computed result
			for (int i = 0; i < this.outputCount; i++) {
				double d = output.getData(i);
				row.getData()[dataIndex++] = this.getInputFormat().format(d,
						this.getPrecision());
			}

			writeRow(tw, row);
		}
		reportDone(false);
		tw.close();
		csv.close();
	}
}
