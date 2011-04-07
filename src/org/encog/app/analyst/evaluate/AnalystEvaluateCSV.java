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
import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.BasicFile;
import org.encog.app.quant.basic.LoadedRow;
import org.encog.app.quant.normalize.ClassItem;
import org.encog.ml.MLRegression;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Used by the analyst to evaluate a CSV file.
 * 
 */
public class AnalystEvaluateCSV extends BasicFile {

	private final Map<String, Integer> classCorrect = new HashMap<String, Integer>();
	private final Map<String, Integer> classCount = new HashMap<String, Integer>();
	private final Map<AnalystField, Integer> columnMapping = new HashMap<AnalystField, Integer>();
	private EncogAnalyst analyst;
	private int inputFieldCount;
	private int outputFieldCount;
	private int idealFieldCount;

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

		this.setAnalyzed(true);
		this.analyst = analyst;

		performBasicCounts();

		this.inputFieldCount = this.analyst.determineInputFieldCount();
		this.outputFieldCount = this.analyst.determineOutputFieldCount();
		this.idealFieldCount = this.getInputHeadings().length - inputFieldCount;

		if (this.getInputHeadings().length != inputFieldCount
				&& this.getInputHeadings().length != (inputFieldCount + outputFieldCount)) {
			throw new AnalystError("Invalid number of columns("
					+ this.getInputHeadings().length + "), must match input("
					+ inputFieldCount + ") count or input+output("
					+ (inputFieldCount + outputFieldCount) + ") count.");
		}

		// perform mapping
		for (int i = 0; i < this.inputHeadings.length; i++) {
			String heading = this.inputHeadings[i];
			AnalystField field = this.analyst.getScript().findNormalizedField(
					heading);
			if (field != null) {
				this.columnMapping.put(field, i);
			}
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
	public PrintWriter prepareOutputFile(File outputFile, int input, int output) {
		try {
			PrintWriter tw = new PrintWriter(new FileWriter(outputFile));

			// write headers, if needed
			if (this.isProduceOutputHeaders()) {
				StringBuilder line = new StringBuilder();

				// first handle the input fields
				for (AnalystField field : this.analyst.getScript()
						.getNormalize().getNormalizedFields()) {
					if (field.isInput()) {
						field.addFieldHeading(line, null,
								this.getOutputFormat());
					}
				}

				// now, handle any ideal fields
				if (this.idealFieldCount > 0) {
					for (AnalystField field : this.analyst.getScript()
							.getNormalize().getNormalizedFields()) {
						if (field.isOutput()) {
							field.addFieldHeading(line, "ideal:",
									this.getOutputFormat());
						}
					}
				}

				// now, handle the output fields
				for (AnalystField field : this.analyst.getScript()
						.getNormalize().getNormalizedFields()) {
					if (field.isOutput()) {
						field.addFieldHeading(line, "output:",
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
			MLRegression method) {

		ReadCSV csv = new ReadCSV(this.getInputFilename().toString(),
				this.isExpectInputHeaders(), this.getInputFormat());

		NeuralData output = null;

		NeuralData input = new BasicNeuralData(method.getInputCount());

		PrintWriter tw = this.prepareOutputFile(outputFile, analyst.getScript()
				.getNormalize().countActiveFields() - 1, 1);

		resetStatus();
		while (csv.next()) {
			updateStatus(false);
			LoadedRow row = new LoadedRow(csv, 1);

			int outputIndex = 0;

			// build the input
			for (AnalystField field : analyst.getScript().getNormalize()
					.getNormalizedFields()) {
				if (this.columnMapping.containsKey(field)) {
					int fieldIndex = this.columnMapping.get(field);
					int columnsNeeded = field.getColumnsNeeded();
					String str = row.getData()[fieldIndex];

					if (field.isInput()) {
						if (columnsNeeded == 1) {
							double d = this.getInputFormat().parse(str);
							input.setData(outputIndex++, field.normalize(d));
						} else if (columnsNeeded > 1) {
							int idx = field.lookup(str);
							double[] e = field.getEq().encode(idx);
							for (int i = 0; i < e.length; i++) {
								input.setData(outputIndex++, e[i]);
							}
						}
					}
				}
			}

			// evaluation data
			output = method.compute(input);
			
			// skip ideal data
			int index = this.inputFieldCount + this.idealFieldCount;

			// display output
			for (AnalystField field : analyst.getScript().getNormalize()
					.getNormalizedFields()) {
				if (this.columnMapping.containsKey(field)) {
					// int fieldIndex = this.columnMapping.get(field);

					if (field.isOutput()) {
						if (field.isClassify()) {
							// classification
							ClassItem cls = field.determineClass(output
									.getData());
							row.getData()[index++] = cls.getName();
						} else {
							// regression
							double n = output.getData(0);
							n = field.deNormalize(n);
							row.getData()[index++] = this.getInputFormat()
									.format(n, this.getPrecision());
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

	public Map<String, Integer> getClassCorrect() {
		return classCorrect;
	}

	public Map<String, Integer> getClassCount() {
		return classCount;
	}

}
