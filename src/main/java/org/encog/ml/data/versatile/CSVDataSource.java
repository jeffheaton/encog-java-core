package org.encog.ml.data.versatile;

import java.io.File;

import org.encog.EncogError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

public class CSVDataSource implements VersatileDataSource {
	
	private ReadCSV reader;
	private final File file;
	private final boolean headers;
	private final CSVFormat format;

	/**
	 * Construct a CSV reader from a filename. The format parameter specifies
	 * the separator character to use, as well as the number format.
	 * 
	 * @param filename
	 *            The filename.
	 * @param headers
	 *            The headers.
	 * @param delim
	 *            The delimiter.
	 */
	public CSVDataSource(final File file, final boolean headers,
			final char delim) {
		this.format = new CSVFormat(CSVFormat.getDecimalCharacter(),
				delim);
		this.headers = headers;
		this.file = file;
	}

	/**
	 * Construct a CSV reader from a filename. Allows a delimiter character to
	 * be specified.
	 * 
	 * @param filename
	 *            The filename.
	 * @param headers
	 *            The headers.
	 * @param format
	 *            The format.
	 */
	public CSVDataSource(final File file, final boolean headers,
			final CSVFormat format) {
		this.file = file;
		this.headers = headers;
		this.format = format;
	}
	
	@Override
	public String[] readLine() {
		if( this.reader==null) {
			throw new EncogError("Please call rewind before reading the file.");
		}
		
		if( this.reader.next() ) {
			int len = this.reader.getColumnCount();
			String[] result = new String[len];
			for(int i=0;i<result.length;i++) {
				result[i]=this.reader.get(i);
			}
			return result;
		} else {
			reader.close();
			return null;
		}
	}

	@Override
	public void rewind() {
		this.reader = new ReadCSV(this.file,this.headers,this.format);
	}

}
