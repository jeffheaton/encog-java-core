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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.encog.Encog;
import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.normalize.AnalystNormalize;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.script.segregate.AnalystSegregate;
import org.encog.app.analyst.script.task.AnalystTask;
import org.encog.util.csv.CSVFormat;

/**
 * Holds a script for the Encog Analyst.
 */
public class AnalystScript {
	
	/**
	 * The default MAX size for a class.
	 */
	public static final int DEFAULT_MAX_CLASS = 50;

	/**
	 * The data fields, these are the raw data from the CSV file.
	 */
	private DataField[] fields;
	
	/**
	 * Information about how to normalize.
	 */
	private final AnalystNormalize normalize = new AnalystNormalize(this);
	
	/**
	 * Information about how to segregate.
	 */
	private final AnalystSegregate segregate = new AnalystSegregate();
	
	/**
	 * Tracks which files were generated.
	 */
	private final Set<String> generated = new HashSet<String>();
	
	/**
	 * The tasks.
	 */
	private final Map<String, AnalystTask> tasks 
		= new HashMap<String, AnalystTask>();
	
	/**
	 * The properties.
	 */
	private final ScriptProperties properties = new ScriptProperties();
	
	/**
	 * The base path.
	 */
	private String basePath;

	/**
	 * Construct an analyst script.
	 */
	public AnalystScript() {
		this.properties.setProperty(ScriptProperties.SETUP_CONFIG_CSV_FORMAT,
				AnalystFileFormat.DECPNT_COMMA);
		this.properties.setProperty(
				ScriptProperties.SETUP_CONFIG_MAX_CLASS_COUNT, 
				DEFAULT_MAX_CLASS);
		this.properties
				.setProperty(ScriptProperties.SETUP_CONFIG_ALLOWED_CLASSES,
						"integer,string");
	}

	/**
	 * Add a task.
	 * @param task The task to add.
	 */
	public final void addTask(final AnalystTask task) {
		this.tasks.put(task.getName(), task);
	}

	/**
	 * Clear all tasks.
	 */
	public final void clearTasks() {
		this.tasks.clear();
	}

	/**
	 * Determine the output format.
	 * @return The output format.
	 */
	public final CSVFormat determineFormat() {
		return getProperties().getPropertyCSVFormat(
				ScriptProperties.SETUP_CONFIG_CSV_FORMAT);
	}

	/**
	 * Determine if input headers are expected.
	 * @param filename The filename.
	 * @return True if headers are expected.
	 */
	public final boolean expectInputHeaders(final String filename) {
		if (isGenerated(filename)) {
			return true;
		} else {
			return this.properties.getPropertyBoolean(
						ScriptProperties.SETUP_CONFIG_INPUT_HEADERS);
		}
	}

	/**
	 * Find the specified data field.  Use name to find, and ignore case.
	 * @param name The name to search for.
	 * @return The specified data field.
	 */
	public final DataField findDataField(final String name) {
		for (final DataField dataField : this.fields) {
			if (dataField.getName().equalsIgnoreCase(name)) {
				return dataField;
			}
		}

		return null;
	}

	/**
	 * Find the specified data field and return its index.
	 * @param df The data field to search for.
	 * @return The index of the specified data field, or -1 if not found.
	 */
	public final int findDataFieldIndex(final DataField df) {
		for (int result = 0; result < this.fields.length; result++) {
			if (df == this.fields[result]) {
				return result;
			}
		}
		return -1;
	}

	/**
	 * Find the specified normalized field.  Search without case.
	 * @param name The name of the field we are searching for.
	 * @param slice The timeslice.
	 * @return The analyst field that was found.
	 */
	public final AnalystField findNormalizedField(final String name, 
			final int slice) {
		for (final AnalystField field : getNormalize().getNormalizedFields()) {
			if (field.getName().equalsIgnoreCase(name)
					&& (field.getTimeSlice() == slice)) {
				return field;
			}
		}

		return null;
	}

	/**
	 * @return The base path.
	 */
	public final String getBasePath() {
		return this.basePath;
	}

	/**
	 * @return the data fields.
	 */
	public final DataField[] getFields() {
		return this.fields;
	}

	/**
	 * @return the normalize
	 */
	public final AnalystNormalize getNormalize() {
		return this.normalize;
	}

	/**
	 * @return The precision.
	 */
	public final int getPrecision() {
		return Encog.DEFAULT_PRECISION;
	}

	/**
	 * @return the properties
	 */
	public final ScriptProperties getProperties() {
		return this.properties;
	}

	/**
	 * @return the segregate
	 */
	public final AnalystSegregate getSegregate() {
		return this.segregate;
	}

	/**
	 * Get the specified task.
	 * @param name The name of the testk.
	 * @return The analyst task.
	 */
	public final AnalystTask getTask(final String name) {
		if (!this.tasks.containsKey(name)) {
			return null;
		}
		return this.tasks.get(name);
	}

	/**
	 * @return The tasks.
	 */
	public final Map<String, AnalystTask> getTasks() {
		return this.tasks;
	}

	/**
	 * Init this script.
	 */
	public final void init() {
		this.normalize.init(this);
	}

	/**
	 * Determine if the specified file was generated.
	 * @param filename The filename to check.
	 * @return True, if the specified file was generated.
	 */
	public final boolean isGenerated(final String filename) {
		return this.generated.contains(filename);
	}

	/**
	 * Load the script.
	 * @param stream The stream to load from.
	 */
	public final void load(final InputStream stream) {
		final ScriptLoad s = new ScriptLoad(this);
		s.load(stream);
	}

	/**
	 * Mark the sepcified filename as generated.
	 * @param filename The filename.
	 */
	public final void markGenerated(final String filename) {
		this.generated.add(filename);
	}

	/**
	 * Resolve the specified filename.
	 * @param sourceID The filename to resolve.
	 * @return The file path.
	 */
	public final File resolveFilename(final String sourceID) {
		final String name = getProperties().getFilename(sourceID);

		if( new File(name).getParent()==null && this.basePath!=null ) {
			return new File(this.basePath, name);
		} else {
			return new File(name);
		}
	}

	/**
	 * Save to the specified output stream.
	 * @param stream The output stream.
	 */
	public final void save(final OutputStream stream) {
		final ScriptSave s = new ScriptSave(this);
		s.save(stream);
	}

	/**
	 * Set the base path.
	 * @param theBasePath The base path.
	 */
	public final void setBasePath(final String theBasePath) {
		this.basePath = theBasePath;
	}

	/**
	 * @param theFields
	 *            the fields to set
	 */
	public final void setFields(final DataField[] theFields) {
		this.fields = theFields;
	}

	public AnalystField findAnalystField(String fieldName) {
		for(AnalystField field : this.normalize.getNormalizedFields() ) {
			if( field.getName().equalsIgnoreCase(fieldName) ) {
				return field;
			}
		}
		return null;
	}
}
