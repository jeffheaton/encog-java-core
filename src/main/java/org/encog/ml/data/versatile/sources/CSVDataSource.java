package org.encog.ml.data.versatile.sources;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.encog.EncogError;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * Allow a CSV file to serve as a source for the versatile data source.
 */
public class CSVDataSource implements VersatileDataSource {
	
	/**
	 * The CSV reader.
	 */
	private ReadCSV reader;
	
	/**
	 * The file to read.
	 */
	private final File file;
	
	/**
	 * True, if the file has headers.
	 */
	private final boolean headers;
	
	/**
	 * The CSV format of the file.
	 */
	private final CSVFormat format;
	
	/**
	 * The index values for each header, if we have headers.
	 */
	private final Map<String,Integer> headerIndex = new HashMap<String,Integer>();

	/**
	 * Construct a CSV source from a filename. The format parameter specifies
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
	 * Construct a CSV source from a filename. Allows a delimiter character to
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

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rewind() {
		this.reader = new ReadCSV(this.file,this.headers,this.format);
		if( this.headerIndex.size()==0 ) {
			for(int i=0;i<this.reader.getColumnNames().size();i++) {
				this.headerIndex.put(this.reader.getColumnNames().get(i), i);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int columnIndex(String name) {
		String name2 = name.toLowerCase();
		if(!this.headerIndex.containsKey(name2)) {
			return -1;
		}
		return this.headerIndex.get(name2);
	}

}
