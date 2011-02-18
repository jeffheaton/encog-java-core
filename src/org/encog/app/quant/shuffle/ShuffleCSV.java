package org.encog.app.quant.shuffle;

import java.io.PrintWriter;

import org.encog.app.quant.basic.BasicFile;
import org.encog.app.quant.basic.LoadedRow;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Randomly shuffle the lines of a CSV file.
 */
public class ShuffleCSV extends BasicFile {
	/**
	 * The buffer size.
	 */
	private int bufferSize;

	/**
	 * The buffer.
	 */
	private LoadedRow[] buffer;

	/**
	 * Remaining in the buffer.
	 */
	private int remaining;

	/** 
	 * @return The buffer size.  This is how many rows of data are loaded(and randomized),
	 * at a time. The default is 5,000.
	 */
	public int getBufferSize() {
		return this.bufferSize;
	}

	/**
	 * Set the buffer size.
	 * @param s The new buffer size.
	 */
	public void setBufferSize(int s) {
		this.bufferSize = s;
		this.buffer = new LoadedRow[this.bufferSize];
	}

	/**
	 * Construct the object
	 */
	public ShuffleCSV() {
		this.setBufferSize(5000);
	}

	/**
	 * Analyze the neural network.
	 * @param inputFile The input file.
	 * @param headers True, if there are headers.
	 * @param format The format of the CSV file.
	 */
	public void analyze(String inputFile, boolean headers, CSVFormat format) {
		this.setInputFilename(inputFile);
		this.setExpectInputHeaders(headers);
		this.setInputFormat(format);

		this.analyzed = true;

		performBasicCounts();
	}

	/**
	 * Load the buffer from the underlying file.
	 * @param csv The CSV file to load from.
	 */
	private void loadBuffer(ReadCSV csv) {
		for (int i = 0; i < this.buffer.length; i++)
			this.buffer[i] = null;

		int index = 0;
		while (csv.next() && (index < this.bufferSize)) {
			LoadedRow row = new LoadedRow(csv);
			buffer[index++] = row;
		}

		this.remaining = index;
	}

	/**
	 * Get the next row from the underlying CSV file.
	 * @param csv The underlying CSV file.
	 * @return The loaded row.
	 */
	private LoadedRow GetNextRow(ReadCSV csv) {
		if (remaining == 0) {
			loadBuffer(csv);
		}

		while (remaining > 0) {
			int index = RangeRandomizer.randomInt(0, this.bufferSize - 1);
			if (this.buffer[index] != null) {
				LoadedRow result = this.buffer[index];
				this.buffer[index] = null;
				this.remaining--;
				return result;
			}
		}
		return null;
	}
	
	/**
	 * Process, and generate the output file.
	 * @param outputFile The output file.
	 */
	public void process(String outputFile) {
		validateAnalyzed();

		ReadCSV csv = new ReadCSV(this.getInputFilename(),
				this.isExpectInputHeaders(), this.getInputFormat());
		LoadedRow row;

		PrintWriter tw = this.prepareOutputFile(outputFile);

		resetStatus();
		while ((row = GetNextRow(csv)) != null) {
			writeRow(tw, row);
			updateStatus(false);
		}
		reportDone(false);
		tw.close();
		csv.close();
	}
}
