/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
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
	 * @param file
	 *            The file.
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
	 * @param file
	 *            The file.
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
