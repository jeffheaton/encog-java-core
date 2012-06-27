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
package org.encog.persist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.encog.util.EngineArray;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.NumberList;

/**
 * Used to read an Encog EG/EGA file. EG files are used to hold Encog objects.
 * EGA files are used to hold Encog Analyst scripts.
 * 
 */
public class EncogReadHelper {

	/**
	 * The file being read.
	 */
	private final BufferedReader reader;

	/**
	 * The lines read from the file.
	 */
	private final List<String> lines = new ArrayList<String>();

	/**
	 * The current section name.
	 */
	private String currentSectionName = "";

	/**
	 * The current subsection name.
	 */
	private String currentSubSectionName = "";

	/**
	 * The current section name.
	 */
	private EncogFileSection section;

	/**
	 * Construct the object.
	 * @param is The input stream.
	 */
	public EncogReadHelper(final InputStream is) {
		this.reader = new BufferedReader(new InputStreamReader(is));
	}

	/**
	 * Close the file.
	 */
	public final void close() {
		try {
			this.reader.close();
		} catch (final IOException e) {
			throw new PersistError(e);
		}
	}

	/**
	 * Read the next section.
	 * 
	 * @return The next section.
	 */
	public final EncogFileSection readNextSection() {

		try {
			String line;
			List<double[]> largeArrays = new ArrayList<double[]>();

			while ((line = this.reader.readLine()) != null) {
				line = line.trim();

				// is it a comment
				if (line.startsWith("//")) {
					continue; 
				}

				// is it a section or subsection
				else if (line.startsWith("[")) {
					// handle previous section
					this.section = new EncogFileSection(
							this.currentSectionName, this.currentSubSectionName);
					this.section.getLines().addAll(this.lines);

					// now begin the new section
					this.lines.clear();
					String s = line.substring(1).trim();
					if (!s.endsWith("]")) {
						throw new PersistError("Invalid section: " + line);
					}
					s = s.substring(0, line.length() - 2);
					final int idx = s.indexOf(':');
					if (idx == -1) {
						this.currentSectionName = s;
						this.currentSubSectionName = "";
					} else {
						if (this.currentSectionName.length() < 1) {
							throw new PersistError(
									"Can't begin subsection when a section has not yet been defined: "
											+ line);
						}

						final String newSection = s.substring(0, idx);
						final String newSubSection = s.substring(idx + 1);

						if (!newSection.equals(this.currentSectionName)) {
							throw new PersistError("Can't begin subsection "
									+ line
									+ ", while we are still in the section: "
									+ this.currentSectionName);
						}

						this.currentSubSectionName = newSubSection;
					}
					this.section.setLargeArrays(largeArrays);
					return this.section;
				} else if (line.length() < 1) {
					continue;
				} else if( line.startsWith("##double")) {
					double[] d = readLargeArray(line);
					largeArrays.add(d);
				} else {
					if (this.currentSectionName.length() < 1) {
						throw new PersistError(
								"Unknown command before first section: " + line);
					}

					this.lines.add(line);
				}

			}

			if (this.currentSectionName.length() == 0) {
				return null;
			}

			this.section = new EncogFileSection(this.currentSectionName,
					this.currentSubSectionName);
			this.section.getLines().addAll(this.lines);
			this.currentSectionName = ""; 
			this.currentSubSectionName = "";
			this.section.setLargeArrays(largeArrays);
			return this.section;
		} catch (final IOException ex) {
			throw new PersistError(ex);
		}

	}

	/**
	 * Called internally to read a large array.
	 * @param line The line containing the beginning of a large array.
	 * @return The array read.
	 * @throws IOException Thrown if an error occurs.
	 */
	private double[] readLargeArray(String line) throws IOException {
		String str = line.substring(9);
		int l = Integer.parseInt(str);
		double[] result = new double[l];
		
		int index = 0;
		while ((line = this.reader.readLine()) != null) {
			line = line.trim();

			// is it a comment
			if (line.startsWith("//")) {
				continue; 
			} else if( line.startsWith("##end")) {
				break;
			}
			
			double[] t = NumberList.fromList(CSVFormat.EG_FORMAT, line);
			EngineArray.arrayCopy(t, 0, result, index, t.length);
			index+=t.length;
		}
		
		return result;
	}
}
