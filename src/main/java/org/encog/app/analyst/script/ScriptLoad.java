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
package org.encog.app.analyst.script;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.prop.PropertyConstraints;
import org.encog.app.analyst.script.prop.PropertyEntry;
import org.encog.app.analyst.script.segregate.AnalystSegregateTarget;
import org.encog.app.analyst.script.task.AnalystTask;
import org.encog.persist.EncogFileSection;
import org.encog.persist.EncogReadHelper;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.csv.CSVFormat;

/**
 * Used to load an Encog Analyst script.
 * 
 */
public class ScriptLoad {

	/**
	 * Column 1.
	 */
	public static final int COLUMN_ONE = 1;
	
	/**
	 * Column 2.
	 */
	public static final int COLUMN_TWO = 2;
	
	/**
	 * Column 3.
	 */
	public static final int COLUMN_THREE = 3;
	
	/**
	 * Column 4.
	 */
	public static final int COLUMN_FOUR = 4;
	
	/**
	 * Column 5.
	 */
	public static final int COLUMN_FIVE = 5;
	
	/**
	 * The script being loaded.
	 */
	private final AnalystScript script;

	/**
	 * Construct a script loader.
	 * @param theScript The script to load into.
	 */
	public ScriptLoad(final AnalystScript theScript) {
		this.script = theScript;
	}

	/**
	 * Handle loading the data classes.
	 * @param section The section being loaded.
	 */
	private void handleDataClasses(final EncogFileSection section) {

		final Map<String, List<AnalystClassItem>> map 
			= new HashMap<String, List<AnalystClassItem>>();

		boolean first = true;
		for (final String line : section.getLines()) {
			if (!first) {
				final List<String> cols = EncogFileSection.splitColumns(line);

				if (cols.size() < COLUMN_FOUR) {
					throw new AnalystError("Invalid data class: " + line);
				}

				final String field = cols.get(0);
				final String code = cols.get(1);
				final String name = cols.get(2);
				final int count = Integer.parseInt(cols.get(3));

				final DataField df = this.script.findDataField(field);

				if (df == null) {
					throw new AnalystError(
							"Attempting to add class to unknown field: " 
							+ name);
				}

				List<AnalystClassItem> classItems;

				if (!map.containsKey(field)) {
					classItems = new ArrayList<AnalystClassItem>();
					map.put(field, classItems);
				} else {
					classItems = map.get(field);
				}

				classItems.add(new AnalystClassItem(code, name, count));
			} else {
				first = false;
			}
		}

		for (final DataField field : this.script.getFields()) {
			if (field.isClass()) {
				final List<AnalystClassItem> classList = map.get(field
						.getName());
				if (classList != null) {
					Collections.sort(classList);
					field.getClassMembers().clear();
					field.getClassMembers().addAll(classList);
				}
			}
		}

	}

	/**
	 * Handle loading data stats.
	 * @param section The section being loaded.
	 */
	private void handleDataStats(final EncogFileSection section) {		
		final List<DataField> dfs = new ArrayList<DataField>();
		boolean first = true;
		for (final String line : section.getLines()) {
			if (!first) {
				final List<String> cols = EncogFileSection.splitColumns(line);
				final String name = cols.get(0);
				final boolean isclass = Integer.parseInt(cols.get(1)) > 0; 
				final boolean iscomplete = Integer.parseInt(cols.get(2)) > 0;
				final boolean isint = 
					Integer.parseInt(cols.get(COLUMN_THREE)) > 0;
				final boolean isreal = 
					Integer.parseInt(cols.get(COLUMN_FOUR)) > 0;
				final double amax = CSVFormat.EG_FORMAT.parse(cols.get(5));
				final double amin = CSVFormat.EG_FORMAT.parse(cols.get(6));
				final double mean = CSVFormat.EG_FORMAT.parse(cols.get(7));
				final double sdev = CSVFormat.EG_FORMAT.parse(cols.get(8));
				final DataField df = new DataField(name);
				df.setClass(isclass);
				df.setComplete(iscomplete);
				df.setInteger(isint);
				df.setReal(isreal);
				df.setMax(amax);
				df.setMin(amin);
				df.setMean(mean);
				df.setStandardDeviation(sdev);
				dfs.add(df);
			} else {
				first = false;
			}
		}

		final DataField[] array = new DataField[dfs.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = dfs.get(i);
		}

		this.script.setFields(array);
	}

	/**
	 * Handle loading the filenames.
	 * @param section The section being loaded.
	 */
	private void handleFilenames(final EncogFileSection section) {

		final Map<String, String> prop = section.parseParams();
		this.script.getProperties().clearFilenames();

		for (final Entry<String, String> e : prop.entrySet()) {
			this.script.getProperties().setFilename(e.getKey(), e.getValue());
		}
	}

	/**
	 * Handle normalization ranges.
	 * @param section The section being loaded.
	 */
	private void handleNormalizeRange(final EncogFileSection section) {
		this.script.getNormalize().getNormalizedFields().clear();
		boolean first = true;
		for (final String line : section.getLines()) {
			if (!first) {
				final List<String> cols = EncogFileSection.splitColumns(line);
				final String name = cols.get(0);
				final String io = cols.get(1);
				final int timeSlice = Integer.parseInt(cols.get(2));
				final String action = cols.get(3);
				final double high = CSVFormat.EG_FORMAT.parse(cols.get(4));
				final double low = CSVFormat.EG_FORMAT.parse(cols.get(5));
				
				boolean isOutput;
				
				if( io.equalsIgnoreCase("input") ) {
					isOutput = false;
				} else if( io.equalsIgnoreCase("output") ) {
					isOutput = true;
				} else {
					throw new AnalystError("Unknown io type:" + io );
				}

				NormalizationAction des = null;
				if (action.equals("range")) {
					des = NormalizationAction.Normalize;
				} else if (action.equals("ignore")) {
					des = NormalizationAction.Ignore;
				} else if (action.equals("pass")) {
					des = NormalizationAction.PassThrough;
				} else if (action.equals("equilateral")) {
					des = NormalizationAction.Equilateral;
				} else if (action.equals("single")) {
					des = NormalizationAction.SingleField;
				} else if (action.equals("oneof")) {
					des = NormalizationAction.OneOf;
				} else {
					throw new AnalystError("Unknown field type:" + action );
				}

				final AnalystField nf = new AnalystField(name, des, high, low);
				nf.setTimeSlice(timeSlice);
				nf.setOutput(isOutput);
				this.script.getNormalize().getNormalizedFields().add(nf);
			} else {
				first = false;
			}
		}

	}

	/**
	 * Handle loading segregation info.
	 * @param section The section being loaded.
	 */
	private void handleSegregateFiles(final EncogFileSection section) {
		final List<AnalystSegregateTarget> nfs 
			= new ArrayList<AnalystSegregateTarget>();
		boolean first = true;
		for (final String line : section.getLines()) {
			if (!first) {
				final List<String> cols = EncogFileSection.splitColumns(line);
				final String filename = cols.get(0);
				final int percent = Integer.parseInt(cols.get(1));

				final AnalystSegregateTarget nf = new AnalystSegregateTarget(
						filename, percent);
				nfs.add(nf);
			} else {
				first = false;
			}
		}

		final AnalystSegregateTarget[] array = new AnalystSegregateTarget[nfs
				.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = nfs.get(i);
		}

		this.script.getSegregate().setSegregateTargets(array);
	}

	/**
	 * Handle loading a task.
	 * @param section The section.
	 */
	private void handleTask(final EncogFileSection section) {
		final AnalystTask task = new AnalystTask(section.getSubSectionName());
		for (final String line : section.getLines()) {
			task.getLines().add(line);
		}
		this.script.addTask(task);
	}

	/**
	 * Load an Encog script.
	 * @param stream The stream to load from.
	 */
	public final void load(final InputStream stream) {
		EncogReadHelper reader = null;

		try {
			EncogFileSection section = null;
			reader = new EncogReadHelper(stream);

			while ((section = reader.readNextSection()) != null) {
				processSubSection(section);
			}

			// init the script
			this.script.init();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Load a generic subsection.
	 * @param section The section to load from.
	 */
	private void loadSubSection(final EncogFileSection section) {
		final Map<String, String> prop = section.parseParams();

		for (final String name : prop.keySet()) {
			final String key = section.getSectionName().toUpperCase() + ":"
					+ section.getSubSectionName().toUpperCase() + "_" + name;
			String value = prop.get(name);
			if (value == null) {
				value = "";
			}
			validateProperty(section.getSectionName(),
					section.getSubSectionName(), name, value);
			this.script.getProperties().setProperty(key, value);
		}
	}

	/**
	 * Process one of the subsections.
	 * @param section The section.
	 */
	private void processSubSection(final EncogFileSection section) {
		final String currentSection = section.getSectionName();
		final String currentSubsection = section.getSubSectionName();

		if (currentSection.equals("SETUP")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("SETUP")
				&& currentSubsection.equalsIgnoreCase("FILENAMES")) {
			handleFilenames(section);
		} else if (currentSection.equals("DATA")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("DATA")
				&& currentSubsection.equalsIgnoreCase("STATS")) {
			handleDataStats(section);
		} else if (currentSection.equals("DATA")
				&& currentSubsection.equalsIgnoreCase("CLASSES")) {
			handleDataClasses(section);
		} else if (currentSection.equals("NORMALIZE")
				&& currentSubsection.equalsIgnoreCase("RANGE")) {
			handleNormalizeRange(section);
		} else if (currentSection.equals("NORMALIZE")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("NORMALIZE")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("CLUSTER")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("SERIES")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("RANDOMIZE")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("SEGREGATE")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("SEGREGATE")
				&& currentSubsection.equalsIgnoreCase("FILES")) {
			handleSegregateFiles(section);
		} else if (currentSection.equals("GENERATE")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("HEADER")
				&& currentSubsection.equalsIgnoreCase("DATASOURCE")) {
			loadSubSection(section);
		} else if (currentSection.equals("ML")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		} else if (currentSection.equals("ML")
				&& currentSubsection.equalsIgnoreCase("TRAIN")) {
			loadSubSection(section);
		} else if (currentSection.equals("TASKS")
				&& (currentSubsection.length() > 0)) {
			handleTask(section);
		} else if (currentSection.equals("BALANCE")
				&& currentSubsection.equalsIgnoreCase("CONFIG")) {
			loadSubSection(section);
		}
	}

	/**
	 * Validate a property.
	 * @param section The section.
	 * @param subSection The sub section.
	 * @param name The name of the property.
	 * @param value The new value for the property.
	 */
	private void validateProperty(final String section,
			final String subSection, final String name, final String value) {
		final PropertyEntry entry = PropertyConstraints.getInstance().getEntry(
				section, subSection, name);
		if (entry == null) {
			throw new AnalystError("Unknown property: "
					+ PropertyEntry.dotForm(section, subSection, name));
		}
		entry.validate(section, subSection, name, value);
	}

}
