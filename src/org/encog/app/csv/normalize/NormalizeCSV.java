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
package org.encog.app.csv.normalize;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;
import org.encog.app.csv.EncogCSVError;
import org.encog.app.csv.basic.BasicFile;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;
import org.encog.util.csv.ReadCSV;
import org.encog.util.logging.EncogLogging;

/**
 * Normalize, or denormalize, a CSV file.
 * 
 */
public class NormalizeCSV extends BasicFile {

	/**
	 * The stats for the fields that were normalized.
	 */
	private NormalizationStats stats;

	/**
	 * Analyze the file.
	 * 
	 * @param file
	 *            The file to analyze.
	 * @param headers
	 *            True, if the file has headers.
	 * @param format
	 *            The format of the CSV file.
	 */
	public final void analyze(final File file, final boolean headers,
			final CSVFormat format) {
		ReadCSV csv = null;

		try {
			resetStatus();
			csv = new ReadCSV(file.toString(), headers, format);

			if (!csv.next()) {
				throw new EncogError("File is empty");
			}

			setInputFilename(file);
			setExpectInputHeaders(headers);
			setInputFormat(format);

			// analyze first row
			final int fieldCount = csv.getColumnCount();
			this.stats = new NormalizationStats(fieldCount);
			this.stats.setFormat(getInputFormat());
			this.stats.setPrecision(getPrecision());

			for (int i = 0; i < fieldCount; i++) {
				this.stats.getStats()[i] = new NormalizedField();
				if (headers) {
					this.stats.getStats()[i].setName(csv.getColumnNames()
							.get(i));
				} else {
					this.stats.getStats()[i].setName("field-" + i);
				}
			}

			// Read entire file to analyze
			do {
				for (int i = 0; i < fieldCount; i++) {
					if (this.stats.getStats()[i].getAction() 
							== NormalizationAction.Normalize) {
						final String str = csv.get(i);

						try {
							final double d = Double.parseDouble(str);
							this.stats.getStats()[i].analyze(d);
						} catch (final NumberFormatException ex) {
							this.stats.getStats()[i].makePassThrough();
						}
					}
				}
				updateStatus(true);
			} while (csv.next() && !shouldStop());
		} finally {
			reportDone(true);
			// Close the CSV file
			if (csv != null) {
				csv.close();
			}
		}

	}

	/**
	 * Analyze a file prior to processing it.
	 * @param inputFilename The input file name.
	 * @param expectInputHeaders Are there input headers.
	 * @param inputFormat The input format.
	 * @param theStats The stats to use.
	 */
	public final void analyze(final File inputFilename,
			final boolean expectInputHeaders, final CSVFormat inputFormat,
			final NormalizationStats theStats) {
		setInputFilename(inputFilename);
		setInputFormat(inputFormat);
		setExpectInputHeaders(expectInputHeaders);
		this.stats = theStats;
		setAnalyzed(true);
	}

	/**
	 * Denormalize the input file.
	 * 
	 * @param targetFile
	 *            The file to write to.
	 */
	public final void deNormalize(final String targetFile) {
		if (this.stats.size() == 0) {
			throw new EncogError(
					"Can't denormalize, there are no stats loaded.");
		}

		final ReadCSV csv = new ReadCSV(getInputFilename().toString(),
				isExpectInputHeaders(), getInputFormat());
		PrintWriter tw;
		try {
			tw = new PrintWriter(new FileWriter(targetFile));
		} catch (final IOException e) {
			throw new EncogCSVError(e);
		}

		if (!csv.next()) {
			throw new EncogError("The source file " + getInputFilename()
					+ " is empty.");
		}

		if (csv.getColumnCount() != this.stats.size()) {
			throw new EncogError("The number of columns in the input file("
					+ csv.getColumnCount() + ") and stats file("
					+ this.stats.size() + ") must match.");
		}

		// write headers, if needed
		if (isExpectInputHeaders()) {
			writeHeaders(tw);
		}

		resetStatus();

		do {
			final StringBuilder line = new StringBuilder();
			updateStatus(false);

			int index = 0;
			for (final NormalizedField stat : this.stats.getStats()) {
				final String str = csv.get(index++);
				if ((line.length() > 0)
						&& (stat.getAction() != NormalizationAction.Ignore)) {
					line.append(getInputFormat().getSeparator());
				}
				switch (stat.getAction()) {
				case PassThrough:
					line.append("\"");
					line.append(str);
					line.append("\"");
					break;
				case Normalize:
					try {
						double d = Double.parseDouble(str);
						d = stat.deNormalize(d);
						line.append(getInputFormat().format(d, 
								this.getPrecision()));
					} catch (final NumberFormatException ex) {
						EncogLogging.log(ex);
					}

					break;
				default: 
					throw new EncogCSVError("Unknown action:"  
							+ stat.getAction());
				}
				
			}
			tw.println(line.toString());
		} while (csv.next() && !shouldStop());

		reportDone(false);
		tw.close();
		csv.close();

	}

	/**
	 * @return The normalization stats.
	 */
	public final NormalizationStats getStats() {
		return this.stats;
	}

	/**
	 * Normalize the input file. Write to the specified file.
	 * 
	 * @param file
	 *            The file to write to.
	 */
	public final void normalize(final File file) {
		if (this.stats.size() < 1) {
			throw new EncogError(
					"Can't normalize yet, file has not been analyzed.");
		}

		this.stats.init();

		ReadCSV csv = null;
		PrintWriter tw = null;
		this.stats.setFormat(getInputFormat());
		this.stats.setPrecision(getPrecision());

		try {
			csv = new ReadCSV(getInputFilename().toString(),
					isExpectInputHeaders(), getInputFormat());

			tw = new PrintWriter(new FileWriter(file));

			// write headers, if needed
			if (isProduceOutputHeaders()) {
				writeHeaders(tw);
			}

			resetStatus();
			// write file contents
			while (csv.next() && !shouldStop()) {
				final StringBuilder line = new StringBuilder();
				updateStatus(false);
				int index = 0;
				for (final NormalizedField stat : this.stats.getStats()) {
					final String str = csv.get(index++);
					if ((line.length() > 0)
							&& (stat.getAction() 
									!= NormalizationAction.Ignore)) {
						line.append(getInputFormat().getSeparator());
					}
					switch (stat.getAction()) {
					case PassThrough:
						line.append("\"");
						line.append(str);
						line.append("\"");
						break;
					case Normalize:
						try {
							double d = getInputFormat().parse(str);
							d = stat.normalize(d);
							line.append(getInputFormat().format(d,
									getPrecision()));
						} catch (final NumberFormatException ex) {
							EncogLogging.log(ex);
						}
						break;

					case OneOf:
						line.append(stat.encode(str));
						break;
					case SingleField:
						line.append(stat.encode(str));
						break;
					case Equilateral:
						line.append(stat.encode(str));
						break;
					default: 
						throw new EncogCSVError("Unknown action:"  
								+ stat.getAction());
					}
				}
				tw.println(line);
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
	 * Read the stats file.
	 * 
	 * @param filename
	 *            The file to read from.
	 */
	public final void readStatsFile(final File filename) {
		final List<NormalizedField> list = new ArrayList<NormalizedField>();

		ReadCSV csv = null;

		try {
			csv = new ReadCSV(filename.toString(), true, CSVFormat.EG_FORMAT);
			while (csv.next() && !shouldStop()) {
				final String type = csv.get(0);
				if (type.equals("Normalize")) {
					final String name = csv.get(1);
					final double ahigh = csv.getDouble(2);
					final double alow = csv.getDouble(3);
					final double nhigh = csv.getDouble(4);
					final double nlow = csv.getDouble(5);
					list.add(new NormalizedField(NormalizationAction.Normalize,
							name, ahigh, alow, nhigh, nlow));
				} else if (type.equals("PassThrough")) {
					final String name = csv.get(1);
					list.add(new NormalizedField(
							NormalizationAction.PassThrough, name));
				} else if (type.equals("Ignore")) {
					final String name = csv.get(1);
					list.add(new NormalizedField(NormalizationAction.Ignore,
							name));
				}
			}
			csv.close();

			this.stats = new NormalizationStats(list.size());
			this.stats.setStats(new NormalizedField[list.size()]);
			for (int i = 0; i < list.size(); i++) {
				this.stats.getStats()[i] = list.get(i);
			}
		} finally {
			if (csv != null) {
				csv.close();
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
		for (final NormalizedField stat : this.stats.getStats()) {
			if ((line.length() > 0)
					&& (stat.getAction() != NormalizationAction.Ignore)) {
				line.append(getInputFormat().getSeparator());
			}

			if (stat.getColumnsNeeded() > 1) {
				line.append(stat.encodeHeaders());
			} else if (stat.getColumnsNeeded() == 1) {
				line.append("\"");
				line.append(stat.getName());
				line.append("\"");
			}
		}
		tw.println(line.toString());
	}

	/**
	 * Write the stats file.
	 * 
	 * @param filename
	 *            The file to write to.
	 */
	public final void writeStatsFile(final File filename) {
		PrintWriter tw = null;

		try {
			tw = new PrintWriter(new FileWriter(filename));

			tw.println("type,name,ahigh,alow,nhigh,nlow");

			for (final NormalizedField stat : this.stats.getStats()) {
				final StringBuilder line = new StringBuilder();
				switch (stat.getAction()) {
				case Ignore:
					line.append("Ignore,\"");
					line.append(stat.getName());
					line.append("\",0,0,0,0");
					break;
				case Normalize:
					line.append("Normalize,");
					line.append("\"");
					line.append(stat.getName());
					line.append("\",");
					final double[] d = { stat.getActualHigh(),
							stat.getActualLow(), stat.getNormalizedHigh(),
							stat.getNormalizedLow() };
					final StringBuilder temp = new StringBuilder();
					NumberList.toList(CSVFormat.EG_FORMAT, temp, d);
					line.append(temp);
					break;
				case PassThrough:
					line.append("PassThrough,\"");
					line.append(stat.getName());
					line.append("\",0,0,0,0");
					break;
				default: 
					throw new EncogCSVError("Unknown action:"  
							+ stat.getAction());

				}

				tw.println(line.toString());
			}
		} catch (final IOException e) {
			throw new EncogCSVError(e);
		} finally {
			// close the stream
			if (tw != null) {
				tw.close();
			}
		}
	}

}
