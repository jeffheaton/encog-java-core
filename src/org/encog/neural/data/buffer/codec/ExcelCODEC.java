package org.encog.neural.data.buffer.codec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.encog.neural.data.buffer.BufferedDataError;
import org.encog.parse.tags.read.ReadXML;
import org.encog.util.csv.CSVFormat;

public class ExcelCODEC implements DataSetCODEC {

	final private File file;
	private ZipFile zipFile;
	private ZipEntry entry;
	private ReadXML xmlIn;
	private int inputCount;
	private int idealCount;

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
		if (this.zipFile != null) {
			try {
				this.zipFile.close();
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
			this.zipFile = new ZipFile(this.file);

			final Enumeration<? extends ZipEntry> entries = this.zipFile
					.entries();

			this.entry = null;

			while (entries.hasMoreElements()) {
				final ZipEntry e = entries.nextElement();
				if (e.getName().equals("xl/worksheets/sheet1.xml")) {
					this.entry = e;
				}
			}

			if (this.entry == null) {
				this.zipFile.close();
				this.zipFile = null;
				throw new BufferedDataError("Could not find worksheet.");
			}

			final InputStream is = this.zipFile.getInputStream(this.entry);
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
