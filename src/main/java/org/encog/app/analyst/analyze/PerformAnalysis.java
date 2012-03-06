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
package org.encog.app.analyst.analyze;

import java.util.List;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.AnalystClassItem;
import org.encog.app.analyst.script.AnalystScript;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.util.CSVHeaders;
import org.encog.app.analyst.util.ConvertStringConst;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * This class is used to perform an analysis of a CSV file. This will help Encog
 * to determine how the fields should be normalized.
 * 
 */
public class PerformAnalysis {

	/**
	 * The file name to analyze.
	 */
	private final String filename;

	/**
	 * True, if headers are present.
	 */
	private final boolean headers;

	/**
	 * The format of this file.
	 */
	private final AnalystFileFormat format;

	/**
	 * The fields to analyze.
	 */
	private AnalyzedField[] fields;

	/**
	 * The script to use.
	 */
	private final AnalystScript script;

	/**
	 * Construct the analysis object.
	 * 
	 * @param theScript
	 *            The script to use.
	 * @param theFilename
	 *            The name of the file to analyze.
	 * @param theHeaders
	 *            True if headers are present.
	 * @param theFormat
	 *            The format of the file being analyzed.
	 */
	public PerformAnalysis(final AnalystScript theScript,
			final String theFilename, final boolean theHeaders,
			final AnalystFileFormat theFormat) {
		this.filename = theFilename;
		this.headers = theHeaders;
		this.format = theFormat;
		this.script = theScript;
	}

	/**
	 * Generate the header fields.
	 * 
	 * @param csv
	 *            The CSV file to use.
	 */
	private void generateFields(final ReadCSV csv) {
		if (this.headers) {
			generateFieldsFromHeaders(csv);
		} else {
			generateFieldsFromCount(csv);
		}
	}

	/**
	 * Generate the fields using counts, no headers provided.
	 * 
	 * @param csv
	 *            The CSV file to use.
	 */
	private void generateFieldsFromCount(final ReadCSV csv) {
		this.fields = new AnalyzedField[csv.getColumnCount()];
		for (int i = 0; i < this.fields.length; i++) {
			this.fields[i] = new AnalyzedField(this.script, "field:" + (i + 1));
		}
	}

	/**
	 * Generate the fields using header values.
	 * 
	 * @param csv
	 *            The CSV file to use.
	 */
	private void generateFieldsFromHeaders(final ReadCSV csv) {
		final CSVHeaders h = new CSVHeaders(csv.getColumnNames());
		this.fields = new AnalyzedField[csv.getColumnCount()];
		for (int i = 0; i < this.fields.length; i++) {
			if (i >= csv.getColumnNames().size()) {
				throw new AnalystError(
						"CSV header count does not match column count");
			}
			this.fields[i] = new AnalyzedField(this.script, h.getHeader(i));
		}
	}

	/**
	 * Perform the analysis.
	 * @param target The Encog analyst object to analyze.
	 */
	public final void process(final EncogAnalyst target) {
		int count = 0;
		final CSVFormat csvFormat = ConvertStringConst
				.convertToCSVFormat(this.format);
		ReadCSV csv = new ReadCSV(this.filename, this.headers, csvFormat);
		
		// pass one, calculate the min/max
		while (csv.next()) {
			if (this.fields == null) {
				generateFields(csv);
			}

			for (int i = 0; i < csv.getColumnCount(); i++) {
				this.fields[i].analyze1(csv.get(i));
			}
			count++;
		}
		
		if( count==0 ) {
			throw new AnalystError("Can't analyze file, it is empty.");
		}


		for (final AnalyzedField field : this.fields) {
			field.completePass1();
		}

		csv.close();

		// pass two, standard deviation
		csv = new ReadCSV(this.filename, this.headers, csvFormat);
		while (csv.next()) {
			for (int i = 0; i < csv.getColumnCount(); i++) {
				this.fields[i].analyze2(csv.get(i));
			}
		}

		for (final AnalyzedField field : this.fields) {
			field.completePass2();
		}

		csv.close();

		String str = this.script.getProperties().getPropertyString(
				ScriptProperties.SETUP_CONFIG_ALLOWED_CLASSES);
		if (str == null) {
			str = "";
		}

		final boolean allowInt = str.contains("int");
		final boolean allowReal = str.contains("real")
				|| str.contains("double");
		final boolean allowString = str.contains("string");

		// remove any classes that did not qualify
		for (final AnalyzedField field : this.fields) {
			if (field.isClass()) {
				if (!allowInt && field.isInteger()) {
					field.setClass(false);
				}

				if (!allowString && (!field.isInteger() && !field.isReal())) {
					field.setClass(false);
				}

				if (!allowReal && field.isReal() && !field.isInteger()) {
					field.setClass(false);
				}
			}
		}

		// merge with existing
		if ((target.getScript().getFields() != null)
				&& (this.fields.length 
						== target.getScript().getFields().length)) {
			for (int i = 0; i < this.fields.length; i++) {
				// copy the old field name
				this.fields[i].setName(target.getScript().getFields()[i]
						.getName());

				if (this.fields[i].isClass()) {
					final List<AnalystClassItem> t = this.fields[i]
							.getAnalyzedClassMembers();
					final List<AnalystClassItem> s = target.getScript()
							.getFields()[i].getClassMembers();

					if (s.size() == t.size()) {
						for (int j = 0; j < s.size(); j++) {
							if (t.get(j).getCode().equals(s.get(j).getCode())) {
								t.get(j).setName(s.get(j).getName());
							}
						}
					}
				}
			}
		}

		// now copy the fields
		final DataField[] df = new DataField[this.fields.length];

		for (int i = 0; i < df.length; i++) {
			df[i] = this.fields[i].finalizeField();
		}

		target.getScript().setFields(df);

	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" filename=");
		result.append(this.filename);
		result.append(", headers=");
		result.append(this.headers);
		result.append("]");
		return result.toString();
	}

}
