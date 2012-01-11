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
package org.encog.app.quant.ninja;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.encog.app.analyst.csv.basic.BasicCachedFile;
import org.encog.app.analyst.csv.basic.FileData;
import org.encog.app.quant.QuantError;
import org.encog.util.csv.ReadCSV;

/**
 * A simple class to convert financial data files into the format used 
 * by NinjaTrader for input.
 */
public class NinjaFileConvert extends BasicCachedFile {
	
	/**
	 * Process the file and output to the target file.
	 * @param target The target file to write to.
	 */
	public final void process(final File target) {
		PrintWriter tw = null;
		
		try {
			ReadCSV csv = new ReadCSV(this.getInputFilename().toString(),
					this.isExpectInputHeaders(), this.getFormat());

			tw = new PrintWriter(new FileWriter(target));

			resetStatus();
			while (csv.next() && !this.shouldStop()) {
				StringBuilder line = new StringBuilder();
				updateStatus(false);
				line.append(this.getColumnData(FileData.DATE, csv));
				line.append(" ");
				line.append(this.getColumnData(FileData.TIME, csv));
				line.append(";");
				line.append(getFormat().format(
						Double.parseDouble(this.getColumnData(FileData.OPEN,
								csv)), this.getPrecision()));
				line.append(";");
				line.append(getFormat().format(
						Double.parseDouble(this.getColumnData(FileData.HIGH,
								csv)), this.getPrecision()));
				line.append(";");
				line.append(getFormat().format(
						Double.parseDouble(this
								.getColumnData(FileData.LOW, csv)),
						this.getPrecision()));
				line.append(";");
				line.append(getFormat().format(
						Double.parseDouble(this.getColumnData(FileData.CLOSE,
								csv)), this.getPrecision()));
				line.append(";");
				line.append(getFormat().format(
						Double.parseDouble(this.getColumnData(FileData.VOLUME,
								csv)), this.getPrecision()));

				tw.println(line.toString());
			}
			reportDone(false);
			csv.close();
			tw.close();
		} catch (IOException ex) {
			throw new QuantError(ex);
		} finally {
			if( tw!=null ) {
				tw.close();
			}
		}
	}
}
