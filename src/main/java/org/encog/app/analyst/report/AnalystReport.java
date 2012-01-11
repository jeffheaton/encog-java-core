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
package org.encog.app.analyst.report;

import java.io.File;
import java.io.IOException;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.script.AnalystClassItem;
import org.encog.app.analyst.script.DataField;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.util.Format;
import org.encog.util.HTMLReport;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.file.FileUtil;

/**
 * Produce a simple report on the makeup of the script and data to be analyued.
 * 
 */
public class AnalystReport {

	/**
	 * Used as a col-span.
	 */
	public static final int FIVE_SPAN = 5;
	
	/**
	 * Used as a col-span.
	 */
	public static final int EIGHT_SPAN = 5;
	
	/**
	 * The analyst to use.
	 */
	private final EncogAnalyst analyst;
	
	/**
	 * The row count.
	 */
	private int rowCount;
	
	/**
	 * The missing count.
	 */
	private int missingCount;

	/**
	 * Construct the report.
	 * @param theAnalyst The analyst to use.
	 */
	public AnalystReport(final EncogAnalyst theAnalyst) {
		this.analyst = theAnalyst;
	}
	
	private void analyzeFile() {
		ScriptProperties prop = this.analyst.getScript().getProperties();
		
		// get filenames, headers & format
		String sourceID = prop.getPropertyString(
				ScriptProperties.HEADER_DATASOURCE_RAW_FILE);

		File sourceFile = this.analyst.getScript().resolveFilename(sourceID);
		CSVFormat inputFormat = this.analyst.getScript().determineFormat();	
		boolean headers = this.analyst.getScript().expectInputHeaders(sourceID);
			
		// read the file
		this.rowCount = 0;
		this.missingCount = 0;
		
		ReadCSV csv = new ReadCSV(sourceFile.toString(),headers,inputFormat);
		while(csv.next()) {
			rowCount++;
			if( csv.hasMissing() )
				missingCount++;
		}
		csv.close();

	}

	/**
	 * Produce the report.
	 * @return The report.
	 */
	public final String produceReport() {
		final HTMLReport report = new HTMLReport();

		analyzeFile();
		report.beginHTML();
		report.title("Encog Analyst Report");
		report.beginBody();
		
		report.h1("General Statistics");
		report.beginTable();
		report.tablePair("Total row count", Format.formatInteger(this.rowCount));
		report.tablePair("Missing row count", Format.formatInteger(this.missingCount));
		report.endTable();

		report.h1("Field Ranges");
		report.beginTable();
		report.beginRow();
		report.header("Name");
		report.header("Class?");
		report.header("Complete?");
		report.header("Int?");
		report.header("Real?");
		report.header("Max");
		report.header("Min");
		report.header("Mean");
		report.header("Standard Deviation");
		report.endRow();

		for (final DataField df : this.analyst.getScript().getFields()) {
			report.beginRow();
			report.cell(df.getName());
			report.cell(Format.formatYesNo(df.isClass()));
			report.cell(Format.formatYesNo(df.isComplete()));
			report.cell(Format.formatYesNo(df.isInteger()));
			report.cell(Format.formatYesNo(df.isReal()));
			report.cell(Format.formatDouble(df.getMax(), FIVE_SPAN));
			report.cell(Format.formatDouble(df.getMin(), FIVE_SPAN));
			report.cell(Format.formatDouble(df.getMean(), FIVE_SPAN));
			report.cell(Format.formatDouble(df.getStandardDeviation(), 
					FIVE_SPAN));
			report.endRow();

			if (df.getClassMembers().size() > 0) {
				report.beginRow();
				report.cell(" ");
				report.beginTableInCell(EIGHT_SPAN);
				report.beginRow();
				report.header("Code");
				report.header("Name");
				report.header("Count");
				report.endRow();
				for (final AnalystClassItem item : df.getClassMembers()) {
					report.beginRow();
					report.cell(item.getCode());
					report.cell(item.getName());
					report.cell(Format.formatInteger(item.getCount()));
					report.endRow();
				}
				report.endTableInCell();
				report.endRow();

			}

		}

		report.endTable();

		report.h1("Normalization");
		report.beginTable();
		report.beginRow();
		report.header("Name");
		report.header("Action");
		report.header("High");
		report.header("Low");
		report.endRow();

		for (final AnalystField item : this.analyst.getScript().getNormalize()
				.getNormalizedFields()) {
			report.beginRow();
			report.cell(item.getName());
			report.cell(item.getAction().toString());
			report.cell(Format.formatDouble(item.getNormalizedHigh(), 
					FIVE_SPAN));
			report.cell(Format.formatDouble(item.getNormalizedLow(), 
					FIVE_SPAN));
			report.endRow();
		}

		report.endTable();
		
		report.h1("Machine Learning");
		report.beginTable();
		report.beginRow();
		report.header("Name");
		report.header("Value");
		report.endRow();

		final String t = this.analyst.getScript().getProperties()
				.getPropertyString(ScriptProperties.ML_CONFIG_TYPE);
		final String a = this.analyst.getScript().getProperties()
				.getPropertyString(ScriptProperties.ML_CONFIG_ARCHITECTURE);
		final String rf = this.analyst
				.getScript()
				.getProperties()
				.getPropertyString(
						ScriptProperties.ML_CONFIG_MACHINE_LEARNING_FILE);

		report.tablePair("Type", t);
		report.tablePair("Architecture", a);
		report.tablePair("Machine Learning File", rf);
		report.endTable();

		report.h1("Files");
		report.beginTable();
		report.beginRow();
		report.header("Name");
		report.header("Filename");
		report.endRow();
		for (final String key : this.analyst.getScript().getProperties()
				.getFilenames()) {
			final String value = this.analyst.getScript().getProperties()
					.getFilename(key);
			report.beginRow();
			report.cell(key);
			report.cell(value);
			report.endRow();
		}
		report.endTable();

		report.endBody();
		report.endHTML();

		return report.toString();
	}

	/**
	 * Produce a report for a filename.
	 * @param filename The filename.
	 */
	public final void produceReport(final File filename) {
		try {
			final String str = produceReport();
			FileUtil.writeFileAsString(filename, str);
		} catch (final IOException ex) {
			throw new AnalystError(ex);
		}
	}
}
