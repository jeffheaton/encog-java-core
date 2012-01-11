/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.ml.data.buffer.codec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.encog.Encog;
import org.encog.ml.data.buffer.BufferedDataError;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.util.csv.CSVFormat;
import org.encog.util.file.ResourceInputStream;
import org.encog.util.logging.EncogLogging;

/**
 * A CODEC that can read/write Microsoft Excel (*.XLSX) files.
 * 
 */
public class ExcelCODEC implements DataSetCODEC {

	/**
	 * The Excel file.
	 */
	private final File file;

	/**
	 * The Excel file that we are reading.
	 */
	private ZipFile readZipFile;

	/**
	 * The current zip entry.
	 */
	private ZipEntry entry;

	/**
	 * XML that is currently being parsed.
	 */
	private ReadXML xmlIn;

	/**
	 * The number of inputs.
	 */
	private int inputCount;

	/**
	 * THe number of ideals.
	 */
	private int idealCount;

	/**
	 * The file stream to write to.
	 */
	private FileOutputStream fos;

	/**
	 * The zip stream to write to.
	 */
	private ZipOutputStream zos;

	/**
	 * A byte buffer to hold the output during an export to XLSX.
	 */
	private ByteArrayOutputStream buffer;

	/**
	 * The XML output.
	 */
	private WriteXML xmlOut;

	/**
	 * THe current row, during an export.
	 */
	private int row;

	/**
	 * Constructor to create Excel from binary.
	 * 
	 * @param theFile
	 *            The CSV file to create.
	 */
	public ExcelCODEC(final File theFile) {

		this.file = theFile;
	}

	/**
	 * Create a CODEC to load data from Excel to binary.
	 * 
	 * @param theFile
	 *            The Excel file to load.
	 * @param theInputCount
	 *            The number of input columns.
	 * @param theIdealCount
	 *            The number of ideal columns.
	 */
	public ExcelCODEC(final File theFile, final int theInputCount,
			final int theIdealCount) {

		this.file = theFile;
		this.inputCount = theInputCount;
		this.idealCount = theIdealCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void close() {
		if (this.readZipFile != null) {
			try {
				this.readZipFile.close();
				this.readZipFile = null;
			} catch (final IOException e) {
				throw new BufferedDataError(e);
			}
		}

		if (this.zos != null) {
			try {
				final ZipEntry theEntry 
					= new ZipEntry("xl/worksheets/sheet1.xml");
				this.xmlOut.endTag();
				this.xmlOut.addAttribute("left", "0.7");
				this.xmlOut.addAttribute("right", "0.7");
				this.xmlOut.addAttribute("top", "0.75");
				this.xmlOut.addAttribute("bottom", "0.75");
				this.xmlOut.addAttribute("header", "0.3");
				this.xmlOut.addAttribute("footer", "0.3");

				this.xmlOut.beginTag("pageMargins");
				this.xmlOut.endTag();
				this.xmlOut.endTag();
				this.xmlOut.endDocument();

				final byte[] b = this.buffer.toByteArray();
				theEntry.setSize(b.length);
				theEntry.setCompressedSize(-1);
				theEntry.setMethod(ZipEntry.DEFLATED);
				this.zos.putNextEntry(theEntry);
				this.zos.write(b);
				this.zos.closeEntry();
				this.zos.close();
				this.zos = null;
			} catch (final IOException e) {
				throw new BufferedDataError(e);
			}
		}

		if (this.fos != null) {
			try {
				this.fos.close();
				this.fos = null;
			} catch (final IOException e) {
				throw new BufferedDataError(e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getIdealSize() {
		return this.idealCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getInputSize() {
		return this.inputCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void prepareRead() {
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

		} catch (final ZipException e) {
			throw new BufferedDataError("Not a valid Excel file.");
		} catch (final IOException e) {
			throw new BufferedDataError(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void prepareWrite(final int recordCount, final int inputSize,
			final int idealSize) {
		this.inputCount = inputSize;
		this.idealCount = idealSize;
		ZipInputStream zis = null;

		try {
			this.fos = new FileOutputStream(this.file);
			this.zos = new ZipOutputStream(this.fos);

			final InputStream is = ResourceInputStream
					.openResourceInputStream("org/encog/data/blank.xlsx");

			zis = new ZipInputStream(is);

			ZipEntry theEntry;

			while (zis.available() > 0) {
				theEntry = zis.getNextEntry();
				if ((entry != null)
			&& !"xl/worksheets/sheet1.xml".equals(entry.getName())) {

					final ZipEntry entry2 = new ZipEntry(theEntry);
					entry2.setCompressedSize(-1);
					this.zos.putNextEntry(entry2);
					final byte[] theBuffer 
						= new byte[(int) entry.getSize()];
					zis.read(theBuffer);
					this.zos.write(theBuffer);
					this.zos.closeEntry();
				}
			}

			zis.close();
			zis = null;

			this.buffer = new ByteArrayOutputStream();
			this.xmlOut = new WriteXML(this.buffer);
			this.xmlOut.beginDocument();
			this.xmlOut
					.addAttribute("xmlns",
		"http://schemas.openxmlformats.org/spreadsheetml/2006/main");
			this.xmlOut
					.addAttribute("xmlns:r",
"http://schemas.openxmlformats.org/officeDocument/2006/relationships");
			this.xmlOut.beginTag("worksheet");
			final StringBuilder d = new StringBuilder();
			d.append(toColumn(this.inputCount + this.idealCount));
			d.append("" + recordCount);
			this.xmlOut.addAttribute("ref", "A1:" + d.toString());
			this.xmlOut.beginTag("dimension");
			this.xmlOut.endTag();
			this.xmlOut.beginTag("sheetViews");
			this.xmlOut.addAttribute("tabSelected", "1");
			this.xmlOut.addAttribute("workbookViewId", "0");
			this.xmlOut.beginTag("sheetView");
			this.xmlOut.endTag();
			this.xmlOut.endTag();
			this.xmlOut.addAttribute("defaultRowHeight", "15");
			this.xmlOut.beginTag("sheetFormatPtr");
			this.xmlOut.endTag();
			this.row = 1;
			this.xmlOut.beginTag("sheetData");

		} catch (final IOException ex) {
			throw new BufferedDataError(ex);
		} finally {
			if( zis!=null ) {
				try {
					zis.close();
				} catch (IOException e) {
					EncogLogging.log(e);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean read(final double[] input, final double[] ideal, double[] significance) {

		int ch;

		while ((ch = this.xmlIn.read()) != -1) {
			if (ch == 0) {
				if (this.xmlIn.is("row", true)) {
					readRow(this.xmlIn, input, ideal);
					return true;
				}
			}
		}

		significance[0] = 1.0;
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	private void readRow(final ReadXML xmlIn, 
			final double[] input,
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
	 * Convert a numeric index, to an Excel column.
	 * 
	 * @param index
	 *            The numeric index.
	 * @return The column, i.e. A or AA.
	 */
	private String toColumn(final int index) {
		final StringBuilder result = new StringBuilder();
		final int first = index / 26;
		final int second = index % 26;
		if (first > 0) {
			result.append((char) ('A' + (first - 1)));
			result.append((char) ('A' + (second - 1)));
		} else {
			result.append((char) ('A' + (second - 1)));
		}
		return result.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void write(final double[] input, final double[] ideal, 
			final double significance) {
		final StringBuilder builder = new StringBuilder();
		builder.append("1");
		builder.append(":");
		builder.append(this.inputCount + this.idealCount);
		this.xmlOut.addAttribute("spans", builder.toString());
		this.xmlOut.addAttribute("r", "" + (this.row++));
		this.xmlOut.beginTag("row");
		int index = 0;
		for (int i = 0; i < this.inputCount; i++) {
			this.xmlOut.addAttribute("r", toColumn(index++));
			this.xmlOut.beginTag("c");
			this.xmlOut.beginTag("v");
			this.xmlOut.addText(CSVFormat.EG_FORMAT.format(input[i],
					Encog.DEFAULT_PRECISION));
			this.xmlOut.endTag();
			this.xmlOut.endTag();
		}

		for (int i = 0; i < this.idealCount; i++) {
			this.xmlOut.addAttribute("r", toColumn(index++));
			this.xmlOut.beginTag("c");
			this.xmlOut.beginTag("v");
			this.xmlOut.addText(CSVFormat.EG_FORMAT.format(ideal[i],
					Encog.DEFAULT_PRECISION));
			this.xmlOut.endTag();
			this.xmlOut.endTag();
		}

		this.xmlOut.endTag();

	}

}
