/*
 * Encog(tm) Workbench v2.5
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.util.simple;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataPair;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * A simple utility to import/export CSV data.
 * 
 * CSV data can be written from any Encog dataset.
 * 
 * CSV data can be imported into any writable Encog dataset.
 * If the data fits into memory, then a BasicNeuralDataSet
 * is a good choice, as it is the fastest Encog dataset.
 * 
 * If the data will not fit into memory a BufferedNeuralDataSet should be used.
 */
public class ImportExportUtility {

	/**
	 * Export a dataset to CSV.  Decimal points will be used.
	 * @param set The dataset to export
	 * @param filename The filename to export to.
	 */
	public static void exportCSV(final NeuralDataSet set,
			final String filename) {
		try {
			final FileOutputStream fos = new FileOutputStream(filename);
			exportCSV(set, fos, CSVFormat.ENGLISH);
			fos.close();
		} catch (IOException ex) {
			throw new EncogError(ex);
		}
	}
	
	/**
	 * Export a dataset to CSV.  Allows a CSV format to be specified.
	 * @param set The dataset to export.
	 * @param filename The filename to export to.
	 * @param format The CSV format.
	 */
	public static void exportCSV(final NeuralDataSet set,
			final String filename, CSVFormat format) {
		try {
			final FileOutputStream fos = new FileOutputStream(filename);
			exportCSV(set, fos, format);
			fos.close();
		} catch (IOException ex) {
			throw new EncogError(ex);
		}
	}

	/**
	 * Export CSV to an output stream.  The CSV format is specified.
	 * @param set The dataset to export.
	 * @param ostream The output stream.
	 * @param format The CSV format.
	 */
	public static void exportCSV(final NeuralDataSet set,
			final OutputStream ostream, CSVFormat format) {

		final PrintStream out = new PrintStream(ostream);
		for (final NeuralDataPair pair : set) {
			final StringBuilder line = new StringBuilder();

			final NeuralData input = pair.getInput();
			final NeuralData ideal = pair.getIdeal();

			// write input
			if (input != null) {
				for (int i = 0; i < input.size(); i++) {
					if (i != 0) {
						line.append(format.getSeparator());
					}			
					line.append(format.format(input.getData(i),
							Encog.DEFAULT_PRECISION));
				}
			}

			// write ideal
			if (ideal != null) {
				for (int i = 0; i < ideal.size(); i++) {
					line.append(format.getSeparator());
					line.append(format.format(ideal.getData(i),
							Encog.DEFAULT_PRECISION));
				}
			}
			out.println(line.toString());
		}
		out.close();
	}

	/**
	 * Import the CSV into the specified dataset.
	 * @param set The dataset to import into.
	 * @param inputSize The number of input values.
	 * @param idealSize The number of ideal values. 
	 * @param filename The filename to import.
	 * @param headers True, if headers are present.
	 */
	public static void importCSV(
			final NeuralDataSet set,
			final int inputSize, 
			final int idealSize,			
			final String filename, 
			final boolean headers) {
		try {
			FileInputStream fis = new FileInputStream(filename);
			importCSV(set, inputSize, idealSize, fis, headers, CSVFormat.ENGLISH);
		} catch (IOException ex) {
			throw new EncogError(ex);
		}
	}

	/**
	 * Import the CSV into the specified dataset.
	 * @param set The dataset to import into.
	 * @param inputSize The number of input values.
	 * @param idealSize The number of ideal values.
	 * @param filename The filename to import from.
	 * @param headers True, if headers are present.
	 * @param format The CSV format.
	 */
	public static void importCSV(final NeuralDataSet set,
			final int inputSize, 
			final int idealSize,
			final String filename, 
			final boolean headers, 
			final CSVFormat format) {
		try {
			FileInputStream fis = new FileInputStream(filename);
			importCSV(set, inputSize, idealSize, fis, headers, format);
		} catch (IOException ex) {
			throw new EncogError(ex);
		}
	}

	/**
	 * Import the CSV into the specified dataset.
	 * @param set The dataset to import into.
	 * @param inputSize The number of input values.
	 * @param idealSize The number of ideal values.
	 * @param istream The InputStream to read from.
	 * @param headers True, if headers are present.
	 * @param format The CSV format.
	 */
	public static void importCSV(final NeuralDataSet set,
			final int inputSize, 
			final int idealSize,
			final InputStream istream, 
			final boolean headers,
			final CSVFormat format) {

		int line = 0;

		final ReadCSV csv = new ReadCSV(istream, false, format);

		while (csv.next()) {
			line++;
			BasicNeuralData input = null, ideal = null;

			if (inputSize + idealSize != csv.getColumnCount()) {
				throw new EncogError("Line #" + line + " has "
						+ csv.getColumnCount()
						+ " columns, but dataset expects "
						+ (inputSize + idealSize) + " columns.");
			}

			if (inputSize > 0) {
				input = new BasicNeuralData(inputSize);
			}
			if (idealSize > 0) {
				ideal = new BasicNeuralData(idealSize);
			}

			final BasicNeuralDataPair pair = new BasicNeuralDataPair(input,
					ideal);
			int index = 0;

			for (int i = 0; i < inputSize; i++) {
				if (input != null)
					input.setData(i, csv.getDouble(index++));
			}
			for (int i = 0; i < idealSize; i++) {
				if (ideal != null)
					ideal.setData(i, csv.getDouble(index++));
			}

			set.add(pair);
		}
		csv.close();
	}

}
