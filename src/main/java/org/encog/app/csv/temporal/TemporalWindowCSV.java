/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.app.csv.temporal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.EncogError;
import org.encog.app.csv.EncogCSVError;
import org.encog.app.csv.basic.BasicFile;
import org.encog.app.csv.util.BarBuffer;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.logging.EncogLogging;

/**
 * This class is used to break a CSV file into temporal windows. This is used
 * for predictive neural networks.
 */
public class TemporalWindowCSV extends BasicFile {
	
	/**
	 * The size of the input window.
	 */
	public static final int DEFAULT_INPUT = 10;

	/**
	 * The size of the input window.
	 */
	private int inputWindow;

	/**
	 * The size of the prediction window.
	 */
	private int predictWindow;

	/**
	 * The fields that are to be processed.
	 */
	private TemporalWindowField[] fields;

	/**
	 * A buffer to hold the data.
	 */
	private BarBuffer buffer;

	/**
	 * Construct the object and set the defaults.
	 */
	public TemporalWindowCSV() {
		super();
		this.inputWindow = DEFAULT_INPUT;
		this.predictWindow = 1;
	}

	/**
	 * Analyze the input file, prior to processing.
	 * 
	 * @param filename
	 *            The filename to process.
	 * @param headers
	 *            True, if the input file has headers.
	 * @param format
	 *            The format of the input file.
	 */
	public final void analyze(final File filename, final boolean headers,
			final CSVFormat format) {
		ReadCSV csv = null;

		try {
			csv = new ReadCSV(filename.toString(), headers, format);
			if (!csv.next() && !shouldStop()) {
				throw new EncogError("Empty file");
			}

			setInputFilename(filename);
			setExpectInputHeaders(headers);
			setInputFormat(format);

			this.fields = new TemporalWindowField[csv.getColumnCount()];

			for (int i = 0; i < csv.getColumnCount(); i++) {
				final String str = csv.get(i);
				String fieldname;

				if (isExpectInputHeaders()) {
					fieldname = csv.getColumnNames().get(i);
				} else {
					fieldname = "Column-" + i;
				}

				this.fields[i] = new TemporalWindowField(fieldname);
				try {
					Double.parseDouble(str);
					this.fields[i].setAction(TemporalType.Input);
				} catch (final NumberFormatException ex) {
					EncogLogging.log(ex);
				}
			}
		} finally {
			if (csv != null) {
				csv.close();
			}
		}
	}

	/**
	 * Count the number of input fields, or fields used to predict.
	 * 
	 * @return The number of input fields.
	 */
	public final int countInputFields() {
		int result = 0;

		for (final TemporalWindowField field : this.fields) {
			if (field.getInput()) {
				result++;
			}
		}

		return result;
	}

	/**
	 * Count the number of fields that are that are in the prediction.
	 * 
	 * @return The number of fields predicted.
	 */
	public final int countPredictFields() {
		int result = 0;

		for (final TemporalWindowField field : this.fields) {
			if (field.getPredict()) {
				result++;
			}
		}

		return result;
	}

	/**
	 * @return the buffer
	 */
	public final BarBuffer getBuffer() {
		return this.buffer;
	}

	/**
	 * @return The fields that are to be processed.
	 */
	public final TemporalWindowField[] getFields() {
		return this.fields;
	}

	/**
	 * @return the inputWindow
	 */
	public final int getInputWindow() {
		return this.inputWindow;
	}

	/**
	 * @return the predictWindow
	 */
	public final int getPredictWindow() {
		return this.predictWindow;
	}

	/**
	 * Process the input file, and write to the output file.
	 * 
	 * @param outputFile
	 *            The output file.
	 */
	public final void process(final File outputFile) {
		if (this.inputWindow < 1) {
			throw new EncogError("Input window must be greater than one.");
		}

		if (this.predictWindow < 1) {
			throw new EncogError("Predict window must be greater than one.");
		}

		final int inputFieldCount = countInputFields();
		final int predictFieldCount = countPredictFields();

		if (inputFieldCount < 1) {
			throw new EncogError("There must be at least 1 input field.");
		}

		if (predictFieldCount < 1) {
			throw new EncogError("There must be at least 1 input field.");
		}

		final int barSize = inputFieldCount + predictFieldCount;

		this.buffer = new BarBuffer(this.inputWindow + this.predictWindow);

		ReadCSV csv = null;
		PrintWriter tw = null;

		try {
			csv = new ReadCSV(getInputFilename().toString(),
					isExpectInputHeaders(), getInputFormat());

			tw = new PrintWriter(new FileWriter(outputFile));

			// write headers, if needed
			if (isExpectInputHeaders()) {
				tw.println(writeHeaders());
			}

			resetStatus();
			while (csv.next() && !shouldStop()) {
				updateStatus(false);
				// begin to populate the bar
				double[] bar = new double[barSize];

				int fieldIndex = 0;
				int barIndex = 0;
				for (final TemporalWindowField field : this.fields) {
					final String str = csv.get(fieldIndex++);

					if ((field.getAction() != TemporalType.Ignore)
							&& (field.getAction() 
							!= TemporalType.PassThrough)) {
						bar[barIndex++] = getInputFormat().parse(str);
					}
					field.setLastValue(str);
				}
				this.buffer.add(bar);

				// if the buffer is full, begin writing out temporal windows
				if (this.buffer.getFull()) {
					final StringBuilder line = new StringBuilder();

					// write passthroughs
					for (final TemporalWindowField field : this.fields) {
						if (field.getAction() == TemporalType.PassThrough) {
							if (line.length() > 0) {
								line.append(",");
							}

							line.append("\"");
							line.append(field.getLastValue());
							line.append("\"");
						}
					}

					// write input
					for (int i = 0; i < this.inputWindow; i++) {
						bar = this.buffer.getData().get(
								this.buffer.getData().size() - 1 - i);

						int index = 0;
						for (final TemporalWindowField field : this.fields) {
							if (field.getInput()) {
								if (line.length() > 0) {
									line.append(',');
								}
								line.append(getInputFormat().format(bar[index],
										getPrecision()));
								index++;
							}
						}
					}

					// write prediction
					for (int i = 0; i < this.predictWindow; i++) {
						bar = this.buffer.getData().get(
								this.predictWindow - 1 - i);

						int index = 0;
						for (final TemporalWindowField field : this.fields) {
							if (field.getPredict()) {
								if (line.length() > 0) {
									line.append(
									getInputFormat().getSeparator());
								}
								line.append(getInputFormat().format(bar[index],
										getPrecision()));
								index++;
							}
						}
					}

					// write the line
					tw.println(line.toString());
				}
			}
		} catch (final IOException e) {
			throw new EncogCSVError(e);
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
	 * @param theInputWindow
	 *            the theInputWindow to set
	 */
	public final void setInputWindow(final int theInputWindow) {
		this.inputWindow = theInputWindow;
	}

	/**
	 * @param thePredictWindow
	 *            the predictWindow to set
	 */
	public final void setPredictWindow(final int thePredictWindow) {
		this.predictWindow = thePredictWindow;
	}

	/**
	 * Format the headings to a string.
	 * 
	 * @return The a string holding the headers, ready to be written.
	 */
	private String writeHeaders() {
		final StringBuilder line = new StringBuilder();

		// write any passthrough fields
		for (final TemporalWindowField field : this.fields) {
			if (field.getAction() == TemporalType.PassThrough) {
				if (line.length() > 0) {
					line.append(getInputFormat().getSeparator());
				}

				line.append(field.getName());
			}
		}

		// write any input fields
		for (int i = 0; i < this.inputWindow; i++) {
			for (final TemporalWindowField field : this.fields) {
				if (field.getInput()) {
					if (line.length() > 0) {
						line.append(",");
					}

					line.append("Input:");
					line.append(field.getName());

					if (i > 0) {
						line.append("(t-");
						line.append(i);
						line.append(")");
					} else {
						line.append("(t)");
					}
				}
			}
		}

		// write any output fields
		for (int i = 0; i < this.predictWindow; i++) {
			for (final TemporalWindowField field : this.fields) {
				if (field.getPredict()) {
					if (line.length() > 0) {
						line.append(",");
					}

					line.append("Predict:");
					line.append(field.getName());

					line.append("(t+");
					line.append(i + 1);
					line.append(")");
				}

			}
		}

		return line.toString();
	}

}
