package org.encog.normalize.target;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.normalize.NormalizationError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

public class NormalizationTargetCSV implements NormalizationTarget {

	private final File outputFile;
	private PrintWriter output;
	private final CSVFormat format;

	public NormalizationTargetCSV(final CSVFormat format, final File file) {
		this.format = format;
		this.outputFile = file;
	}

	public NormalizationTargetCSV(final File file) {
		this.format = CSVFormat.ENGLISH;
		this.outputFile = file;
	}

	public void close() {
		this.output.close();
	}

	public void open() {
		try {
			final FileWriter outFile = new FileWriter(this.outputFile);
			this.output = new PrintWriter(outFile);
		} catch (final IOException e) {
			throw (new NormalizationError(e));
		}
	}

	public void write(final double[] data, final int inputCount) {
		final StringBuilder result = new StringBuilder();
		NumberList.toList(this.format, result, data);
		this.output.println(result.toString());
	}

}
