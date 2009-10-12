/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */
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
