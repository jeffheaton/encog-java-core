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
	 * Set the source file.  This is useful if you want to use pre-existing stats 
	 * to normalize something and skip the analyze step.
	 * @param file The file to use.
	 * @param headers True, if headers are to be expected.
	 * @param format The format of the CSV file.
	 */
	public void setSourceFile(File file, boolean headers, CSVFormat format) {
		this.setInputFilename(file);
		this.setExpectInputHeaders(headers);
		this.setInputFormat(format);
	}

	/**
	 * Analyze the file.
	 * @param file The file to analyze.
	 * @param headers True, if the file has headers.
	 * @param format The format of the CSV file.
	 */
	public void analyze(File file, boolean headers, CSVFormat format) {
		ReadCSV csv = null;

		try {
			resetStatus();
			csv = new ReadCSV(file.toString(), headers, format);

			if (!csv.next()) {
				throw new EncogError("File is empty");
			}

			this.setInputFilename(file);
			this.setExpectInputHeaders(headers);
			this.setInputFormat(format);

			// analyze first row
			int fieldCount = csv.getColumnCount();
			this.stats = new NormalizationStats(fieldCount);
			this.stats.setFormat(this.getInputFormat());
			this.stats.setPrecision(this.getPrecision());

			for (int i = 0; i < fieldCount; i++) {
				stats.getStats()[i] = new NormalizedField();
				if (headers)
					stats.getStats()[i].setName(csv.getColumnNames().get(i));
				else
					stats.getStats()[i].setName("field-" + i);
			}

			// Read entire file to analyze
			do {
				for (int i = 0; i < fieldCount; i++) {
					if (stats.getStats()[i].getAction() == NormalizationAction.Normalize) {
						String str = csv.get(i);

						try {
							double d = Double.parseDouble(str);
							stats.getStats()[i].analyze(d);
						} catch (NumberFormatException ex) {
							stats.getStats()[i].makePassThrough();
						}
					}
				}
				updateStatus(true);
			} while (csv.next()&& !this.shouldStop());
		} finally {
			reportDone(true);
			// Close the CSV file
			if (csv != null)
				csv.close();
		}

	}

	/**
	 * Denormalize the input file.
	 * @param targetFile The file to write to.
	 */
	public void deNormalize(String targetFile) {
		if (this.stats.size() == 0) {
			throw new EncogError(
					"Can't denormalize, there are no stats loaded.");
		}

		ReadCSV csv = new ReadCSV(this.getInputFilename().toString(),
				this.isExpectInputHeaders(), this.getInputFormat());
		PrintWriter tw;
		try {
			tw = new PrintWriter(new FileWriter(targetFile));
		} catch (IOException e) {
			throw new EncogCSVError(e);
		}

		if (!csv.next()) {
			throw new EncogError("The source file " + this.getInputFilename()
					+ " is empty.");
		}

		if (csv.getColumnCount() != this.stats.size()) {
			throw new EncogError("The number of columns in the input file("
					+ csv.getColumnCount() + ") and stats file("
					+ this.stats.size() + ") must match.");
		}

		// write headers, if needed
		if (this.isExpectInputHeaders()) {
			writeHeaders(tw);
		}

		resetStatus();

		do {
			StringBuilder line = new StringBuilder();
			updateStatus(false);

			int index = 0;
			for (NormalizedField stat : this.stats.getStats()) {
				String str = csv.get(index++);
				if (line.length() > 0
						&& stat.getAction() != NormalizationAction.Ignore)
					line.append(this.getInputFormat().getSeparator());
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
						line.append(this.getInputFormat().format(d, 10));
					} catch (NumberFormatException ex) {

					}

					break;
				}
			}
			tw.println(line.toString());
		} while (csv.next()&& !this.shouldStop());

		reportDone(false);
		tw.close();
		csv.close();

	}

	/**
	 * Write the headers.
	 * @param tw The output stream.
	 */
	private void writeHeaders(PrintWriter tw) {
		StringBuilder line = new StringBuilder();
		for (NormalizedField stat : this.stats.getStats()) {
			if (line.length() > 0
					&& stat.getAction() != NormalizationAction.Ignore)
				line.append(this.getInputFormat().getSeparator());

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
	 * Normalize the input file.  Write to the specified file.
	 * @param file The file to write to.
	 */
	public void normalize(File file) {
		if (this.stats.size() < 1)
			throw new EncogError(
					"Can't normalize yet, file has not been analyzed.");

		stats.init();

		ReadCSV csv = null;
		PrintWriter tw = null;
		this.stats.setFormat(this.getInputFormat());
		this.stats.setPrecision(this.getPrecision());


		try {
			csv = new ReadCSV(this.getInputFilename().toString(),
					this.isExpectInputHeaders(), this.getInputFormat());

			tw = new PrintWriter(new FileWriter(file));

			// write headers, if needed
			if (this.isProduceOutputHeaders()) {
				writeHeaders(tw);
			}

			resetStatus();
			// write file contents
			while (csv.next()&& !this.shouldStop()) {
				StringBuilder line = new StringBuilder();
				updateStatus(false);
				int index = 0;
				for (NormalizedField stat : this.stats.getStats()) {
					String str = csv.get(index++);
					if (line.length() > 0
							&& stat.getAction() != NormalizationAction.Ignore)
						line.append(this.getInputFormat().getSeparator());
					switch (stat.getAction()) {
					case PassThrough:
						line.append("\"");
						line.append(str);
						line.append("\"");
						break;
					case Normalize:
						try {
							double d = this.getInputFormat().parse(str);
							d = stat.normalize(d);
							line.append(this.getInputFormat().format(d,
									this.getPrecision()));
						} catch (NumberFormatException ex) {

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
					}
				}
				tw.println(line);
			}
		} catch (IOException e) {
			throw new EncogCSVError(e);
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

	/**
	 * Read the stats file.
	 * @param filename The file to read from.
	 */
	public void readStatsFile(File filename) {
		List<NormalizedField> list = new ArrayList<NormalizedField>();

		ReadCSV csv = null;

		try {
			csv = new ReadCSV(filename.toString(), true, CSVFormat.EG_FORMAT);
			while (csv.next()&& !this.shouldStop()) {
				String type = csv.get(0);
				if (type.equals("Normalize")) {
					String name = csv.get(1);
					double ahigh = csv.getDouble(2);
					double alow = csv.getDouble(3);
					double nhigh = csv.getDouble(4);
					double nlow = csv.getDouble(5);
					list.add(new NormalizedField(NormalizationAction.Normalize,
							name, ahigh, alow, nhigh, nlow));
				} else if (type.equals("PassThrough")) {
					String name = csv.get(1);
					list.add(new NormalizedField(
							NormalizationAction.PassThrough, name));
				} else if (type.equals("Ignore")) {
					String name = csv.get(1);
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
			if (csv != null)
				csv.close();
		}
	}

	/**
	 * Write the stats file.
	 * @param filename The file to write to.
	 */
	public void writeStatsFile(File filename) {
		PrintWriter tw = null;

		try {
			tw = new PrintWriter(new FileWriter(filename));

			tw.println("type,name,ahigh,alow,nhigh,nlow");

			for (NormalizedField stat : this.stats.getStats()) {
				StringBuilder line = new StringBuilder();
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
					double[] d = { stat.getActualHigh(), stat.getActualLow(),
							stat.getNormalizedHigh(), stat.getNormalizedLow() };
					StringBuilder temp = new StringBuilder();
					NumberList.toList(CSVFormat.EG_FORMAT, temp, d);
					line.append(temp);
					break;
				case PassThrough:
					line.append("PassThrough,\"");
					line.append(stat.getName());
					line.append("\",0,0,0,0");
					break;

				}

				tw.println(line.toString());
			}
		} catch (IOException e) {
			throw new EncogCSVError(e);
		} finally {
			// close the stream
			if (tw != null)
				tw.close();
		}
	}

	/**
	 * @return The normalization stats.
	 */
	public NormalizationStats getStats() {
		return this.stats;
	}

	public void analyze(File inputFilename, boolean expectInputHeaders,
			CSVFormat inputFormat, NormalizationStats stats) {
		this.setInputFilename( inputFilename);
		this.setInputFormat( inputFormat);
		this.setExpectInputHeaders( expectInputHeaders);
		this.stats = stats;
		this.setAnalyzed( true);
	}

}
