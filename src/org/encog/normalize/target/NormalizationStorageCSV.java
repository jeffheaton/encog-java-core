/*
 * Encog(tm) Core v2.5 
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

package org.encog.normalize.target;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.normalize.NormalizationError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

/**
 * Store normalized data to a CSV file.
 */
public class NormalizationStorageCSV implements NormalizationStorage {

	/**
	 * The output file.
	 */
	private final File outputFile;
	
	/**
	 * The output writer.
	 */
	private PrintWriter output;
	
	/**
	 * The CSV format to use.
	 */
	private final CSVFormat format;

	/**
	 * Construct a CSV storage object from the specified file.
	 * @param format The format to use.
	 * @param file The file to write the CSV to.
	 */
	public NormalizationStorageCSV(final CSVFormat format, final File file) {
		this.format = format;
		this.outputFile = file;
	}

	/**
	 * Construct a CSV storage object from the specified file.
	 * @param file The file to write the CSV to.
	 */
	public NormalizationStorageCSV(final File file) {
		this.format = CSVFormat.ENGLISH;
		this.outputFile = file;
	}

	/**
	 * Close the CSV file.
	 */
	public void close() {
		this.output.close();
	}

	/**
	 * Open the CSV file.
	 */
	public void open() {
		try {
			final FileWriter outFile = new FileWriter(this.outputFile);
			this.output = new PrintWriter(outFile);
		} catch (final IOException e) {
			throw (new NormalizationError(e));
		}
	}

	/**
	 * Write an array.
	 * 
	 * @param data
	 *            The data to write.
	 * @param inputCount
	 *            How much of the data is input.
	 */
	public void write(final double[] data, final int inputCount) {
		final StringBuilder result = new StringBuilder();
		NumberList.toList(this.format, result, data);
		this.output.println(result.toString());
	}

}
