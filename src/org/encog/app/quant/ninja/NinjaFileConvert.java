package org.encog.app.quant.ninja;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.BasicCachedFile;
import org.encog.app.quant.basic.FileData;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * A simple class to convert financial data files into the format used by NinjaTrader for
 * input.
 */
public class NinjaFileConvert extends BasicCachedFile {
	
	/**
	 * Process the file and output to the target file.
	 * @param target The target file to write to.
	 */
	public void process(File target) {
		try {
			ReadCSV csv = new ReadCSV(this.getInputFilename().toString(),
					this.isExpectInputHeaders(), this.getInputFormat());

			PrintWriter tw = new PrintWriter(new FileWriter(target));

			resetStatus();
			while (csv.next() && !this.shouldStop()) {
				StringBuilder line = new StringBuilder();
				updateStatus(false);
				line.append(this.getColumnData(FileData.DATE, csv));
				line.append(" ");
				line.append(this.getColumnData(FileData.TIME, csv));
				line.append(";");
				line.append(getInputFormat().format(
						Double.parseDouble(this.getColumnData(FileData.OPEN,
								csv)), this.getPrecision()));
				line.append(";");
				line.append(getInputFormat().format(
						Double.parseDouble(this.getColumnData(FileData.HIGH,
								csv)), this.getPrecision()));
				line.append(";");
				line.append(getInputFormat().format(
						Double.parseDouble(this
								.getColumnData(FileData.LOW, csv)),
						this.getPrecision()));
				line.append(";");
				line.append(getInputFormat().format(
						Double.parseDouble(this.getColumnData(FileData.CLOSE,
								csv)), this.getPrecision()));
				line.append(";");
				line.append(getInputFormat().format(
						Double.parseDouble(this.getColumnData(FileData.VOLUME,
								csv)), this.getPrecision()));

				tw.println(line.toString());
			}
			reportDone(false);
			csv.close();
			tw.close();
		} catch (IOException ex) {
			throw new QuantError(ex);
		}
	}

	/**
	 * Analyze the input file.
	 * @param input The name of the input file.
	 * @param headers True, if headers are present.
	 * @param format The format of the input file.
	 */
	public void analyze(File input, boolean headers, CSVFormat format) {
		super.analyze(input, headers, format);
	}

}
