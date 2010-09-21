/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog.neural.data.buffer.codec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.encog.neural.data.buffer.BufferedDataError;
import org.encog.parse.tags.read.ReadXML;
import org.encog.util.csv.CSVFormat;

public class ExcelCODEC implements DataSetCODEC {

	final private File file;
	private ZipFile readZipFile;
	private ZipEntry entry;
	private ReadXML xmlIn;
	private int inputCount;
	private int idealCount;
	private FileOutputStream fos;
    private ZipOutputStream zos;

	/**
	 * Constructor to create Excel from binary.
	 * 
	 * @param file
	 *            The CSV file to create.
	 */
	public ExcelCODEC(final File file) {

		this.file = file;
	}

	/**
	 * Create a CODEC to load data from Excel to binary.
	 * 
	 * @param file
	 *            The Excel file to load.
	 * @param inputCount
	 *            The number of input columns.
	 * @param idealCount
	 *            The number of ideal columns.
	 */
	public ExcelCODEC(final File file, final int inputCount,
			final int idealCount) {

		this.file = file;
		this.inputCount = inputCount;
		this.idealCount = idealCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		if (this.readZipFile != null) {
			try {
				this.readZipFile.close();
			} catch (final IOException e) {
				throw new BufferedDataError(e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIdealSize() {
		return this.idealCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInputSize() {
		return this.inputCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareRead() {
		try {
			this.readZipFile = new ZipFile(this.file);

			final Enumeration<? extends ZipEntry> entries = this.readZipFile
					.entries();

			this.entry = null;

			while (entries.hasMoreElements()) {
				final ZipEntry e = entries.nextElement();
				if (e.getName().equals("xl/worksheets/sheet1.xml")) {
					this.entry = e;
				}
			}

			if (this.entry == null) {
				this.readZipFile.close();
				this.readZipFile = null;
				throw new BufferedDataError("Could not find worksheet.");
			}

			final InputStream is = this.readZipFile.getInputStream(this.entry);
			this.xmlIn = new ReadXML(is);

			System.out.println(this.entry.getName());
		} catch (final IOException e) {
			throw new BufferedDataError(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareWrite(final int recordCount, final int inputSize,
			final int idealSize) {
		this.inputCount = inputSize;
		this.idealCount = idealSize;
		
		try {
		this.fos = new FileOutputStream(this.file);
		this.zos = new ZipOutputStream(fos);
		} catch(IOException ex) {
			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean read(final double[] input, final double[] ideal) {

		int ch;

		while ((ch = this.xmlIn.read()) != -1) {
			if (ch == 0) {
				if (this.xmlIn.is("row", true)) {
					readRow(this.xmlIn, input, ideal);
					return true;
				}
			}
		}

		return false;
	}

	private void readRow(final ReadXML xmlIn, final double[] input,
			final double[] ideal) {
		int ch;

		int index = 0;
		while ((ch = this.xmlIn.read()) != -1) {
			if (ch == 0) {
				if (this.xmlIn.is("v", true)) {
					final String str = this.xmlIn.readTextToTag();
					final double d = CSVFormat.ENGLISH.parse(str);
					if (index < input.length) {
						input[index] = d;
					} else {
						ideal[index - input.length] = d;
					}
					index++;
				} else if (this.xmlIn.is("row", false)) {
					break;
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final double[] input, final double[] ideal) {
		// TODO Auto-generated method stub

	}

}
