package org.encog.app.quant.sort;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.BasicFile;
import org.encog.app.quant.basic.LoadedRow;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Used to sort a CSV file by one, or more, fields.
 */
public class SortCSV extends BasicFile {

	/**
	 * @return Used to specify the sort order.
	 */
	public List<SortedField> getSortOrder() {
		return this.sortOrder;
	}

	/**
	 * The loaded rows.
	 */
	private List<LoadedRow> data = new ArrayList<LoadedRow>();

	/**
	 * The sort order.
	 */
	private List<SortedField> sortOrder = new ArrayList<SortedField>();

	/**
	 * Read the input file.
	 */
	private void readInputFile() {
		resetStatus();

		ReadCSV csv = new ReadCSV(getInputFilename(), isExpectInputHeaders(),
				getInputFormat());
		while (csv.next()) {
			updateStatus("Reading input file");
			LoadedRow row = new LoadedRow(csv);
			this.data.add(row);
		}

		this.setColumnCount(csv.getColumnCount());

		if (this.isExpectInputHeaders()) {
			this.setInputHeadings(new String[csv.getColumnNames().size()]);
			for (int i = 0; i < csv.getColumnNames().size(); i++) {
				this.getInputHeadings()[i] = csv.getColumnNames().get(i);
			}
		}

		csv.close();
	}

	/**
	 * Sort the loaded data.
	 */
	private void sortData() {
		Comparator<LoadedRow> comp = new RowComparator(this);
		Collections.sort(this.data, comp);
	}

	/**
	 * Write the sorted output file. 
	 * @param outputFile The name of the output file.
	 */
	private void writeOutputFile(String outputFile) {
		PrintWriter tw = this.prepareOutputFile(outputFile);
		boolean[] nonNumeric = new boolean[this.getColumnCount()];
		boolean first = true;

		resetStatus();

		// write the file
		for (LoadedRow row : this.data) {
			updateStatus("Writing output");
			// for the first row, determine types
			if (first) {
				for (int i = 0; i < this.getColumnCount(); i++) {
					try {
						double d;
						String str = row.getData()[i];
						Double.parseDouble(str);
						nonNumeric[i] = false;
					} catch (Exception ex) {
						nonNumeric[i] = true;
					}
				}
				first = false;
			}

			// write the row
			StringBuilder line = new StringBuilder();

			for (int i = 0; i < this.getColumnCount(); i++) {
				if (i > 0) {
					line.append(",");
				}

				if (nonNumeric[i]) {
					line.append("\"");
					line.append(row.getData()[i]);
					line.append("\"");
				} else {
					line.append(row.getData()[i]);
				}
			}

			tw.println(line.toString());
		}

		reportDone("Writing output");

		// close the file

		tw.close();
	}

	/**
	 * Process, and sort the files.
	 * @param inputFile The input file.
	 * @param outputFile The output file.
	 * @param headers True, if headers are to be used.
	 * @param format The format of the file.
	 */
	public void process(String inputFile, String outputFile, boolean headers,
			CSVFormat format) {
		this.setInputFilename(inputFile);
		this.setExpectInputHeaders(headers);
		this.setInputFormat(format);

		readInputFile();
		sortData();
		writeOutputFile(outputFile);
	}

}
