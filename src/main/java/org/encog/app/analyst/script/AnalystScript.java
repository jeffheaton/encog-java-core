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
package org.encog.app.analyst.script;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.encog.Encog;
import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.script.ml.ScriptOpcode;
import org.encog.app.analyst.script.normalize.AnalystField;
import org.encog.app.analyst.script.normalize.AnalystNormalize;
import org.encog.app.analyst.script.process.AnalystProcess;
import org.encog.app.analyst.script.prop.ScriptProperties;
import org.encog.app.analyst.script.segregate.AnalystSegregate;
import org.encog.app.analyst.script.task.AnalystTask;
import org.encog.app.analyst.util.FieldDirection;
import org.encog.util.arrayutil.ClassItem;
import org.encog.util.arrayutil.NormalizationAction;
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
	 * Information about the process command.
	 */
	private final AnalystProcess process = new AnalystProcess();

	/**
	 * Tracks which files were generated.
	 */
	private final Set<String> generated = new HashSet<String>();

	private final List<ScriptOpcode> opcodes = new ArrayList<ScriptOpcode>();

	/**
	 * The tasks.
	 */
	private final Map<String, AnalystTask> tasks = new HashMap<String, AnalystTask>();

	/**
	 * The properties.
	 */
	private final ScriptProperties properties = new ScriptProperties();

	/**
	 * The base path.
	 */
	private String basePath;

	private transient double defaultNormalizedRangeLow = 0;
	private transient double defaultNormalizedRangeHigh = 1;

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
	 * 
	 * @param task
	 *            The task to add.
	 */
	public void addTask(final AnalystTask task) {
		this.tasks.put(task.getName(), task);
	}

	/**
	 * Clear all tasks.
	 */
	public void clearTasks() {
		this.tasks.clear();
	}

	/**
	 * Determine the output format.
	 * 
	 * @return The output format.
	 */
	public CSVFormat determineFormat() {
		return getProperties().getPropertyCSVFormat(
				ScriptProperties.SETUP_CONFIG_CSV_FORMAT);
	}

	/**
	 * Determine if input headers are expected.
	 * 
	 * @param filename
	 *            The filename.
	 * @return True if headers are expected.
	 */
	public boolean expectInputHeaders(final String filename) {
		if (isGenerated(filename)) {
			return true;
		} else {
			return this.properties
					.getPropertyBoolean(ScriptProperties.SETUP_CONFIG_INPUT_HEADERS);
		}
	}

	/**
	 * Find the specified data field. Use name to find, and ignore case.
	 * 
	 * @param name
	 *            The name to search for.
	 * @return The specified data field.
	 */
	public DataField findDataField(final String name) {
		for (final DataField dataField : this.fields) {
			if (dataField.getName().equalsIgnoreCase(name)) {
				return dataField;
			}
		}

		return null;
	}

	/**
	 * Find the specified data field and return its index.
	 * 
	 * @param df
	 *            The data field to search for.
	 * @return The index of the specified data field, or -1 if not found.
	 */
	public int findDataFieldIndex(final DataField df) {
		for (int result = 0; result < this.fields.length; result++) {
			if (df == this.fields[result]) {
				return result;
			}
		}
		return -1;
	}

	/**
	 * Find the specified normalized field. Search without case.
	 * 
	 * @param name
	 *            The name of the field we are searching for.
	 * @param slice
	 *            The timeslice.
	 * @return The analyst field that was found.
	 */
	public AnalystField findNormalizedField(final String name, final int slice) {
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
	public String getBasePath() {
		return this.basePath;
	}

	/**
	 * @return the data fields.
	 */
	public DataField[] getFields() {
		return this.fields;
	}

	/**
	 * @return the normalize
	 */
	public AnalystNormalize getNormalize() {
		return this.normalize;
	}

	/**
	 * @return The precision.
	 */
	public int getPrecision() {
		return Encog.DEFAULT_PRECISION;
	}

	/**
	 * @return the properties
	 */
	public ScriptProperties getProperties() {
		return this.properties;
	}

	/**
	 * @return the segregate
	 */
	public AnalystSegregate getSegregate() {
		return this.segregate;
	}

	/**
	 * Get the specified task.
	 * 
	 * @param name
	 *            The name of the testk.
	 * @return The analyst task.
	 */
	public AnalystTask getTask(final String name) {
		if (!this.tasks.containsKey(name)) {
			return null;
		}
		return this.tasks.get(name);
	}

	/**
	 * @return The tasks.
	 */
	public Map<String, AnalystTask> getTasks() {
		return this.tasks;
	}

	/**
	 * Init this script.
	 */
	public void init() {
		this.normalize.init(this);
	}

	/**
	 * Determine if the specified file was generated.
	 * 
	 * @param filename
	 *            The filename to check.
	 * @return True, if the specified file was generated.
	 */
	public boolean isGenerated(final String filename) {
		return this.generated.contains(filename);
	}

	/**
	 * Load the script.
	 * 
	 * @param stream
	 *            The stream to load from.
	 */
	public void load(final InputStream stream) {
		final ScriptLoad s = new ScriptLoad(this);
		s.load(stream);
	}

	/**
	 * Mark the sepcified filename as generated.
	 * 
	 * @param filename
	 *            The filename.
	 */
	public void markGenerated(final String filename) {
		this.generated.add(filename);
	}

	/**
	 * Resolve the specified filename.
	 * 
	 * @param sourceID
	 *            The filename to resolve.
	 * @return The file path.
	 */
	public File resolveFilename(final String sourceID) {
		final String name = getProperties().getFilename(sourceID);

		if (new File(name).getParent() == null && this.basePath != null) {
			return new File(this.basePath, name);
		} else {
			return new File(name);
		}
	}

	/**
	 * Save to the specified output stream.
	 * 
	 * @param stream
	 *            The output stream.
	 */
	public void save(final OutputStream stream) {
		final ScriptSave s = new ScriptSave(this);
		s.save(stream);
	}

	/**
	 * Set the base path.
	 * 
	 * @param theBasePath
	 *            The base path.
	 */
	public void setBasePath(final String theBasePath) {
		this.basePath = theBasePath;
	}

	/**
	 * @param theFields
	 *            the fields to set
	 */
	public void setFields(final DataField[] theFields) {
		this.fields = theFields;
	}

	public AnalystField findAnalystField(String fieldName) {
		for (AnalystField field : this.normalize.getNormalizedFields()) {
			if (field.getName().equalsIgnoreCase(fieldName)) {
				return field;
			}
		}
		return null;
	}

	public AnalystProcess getProcess() {
		return process;
	}

	/**
	 * @return the opcodes
	 */
	public List<ScriptOpcode> getOpcodes() {
		return opcodes;
	}

	public boolean hasClasses() {
		for (AnalystField field : this.getNormalize().getNormalizedFields()) {
			if (field.getAction() != NormalizationAction.Ignore) {
				DataField df = this.findDataField(field.getName());
				if (df.isClass()) {
					return true;
				}
			}
		}
		return false;
	}

	public AnalystField defineField(String fieldName, FieldDirection d, NormalizationAction action, 
			double theActualHigh, double theActualLow) {
		AnalystField field = new AnalystField();

		if (action == NormalizationAction.Equilateral
				|| action == NormalizationAction.OneOf) {
			throw new AnalystError(
					"Must use defineClass if you are going to use Equilateral or OneOf");
		}

		// add underlying raw field
		DataField df = new DataField(fieldName);
		df.setMax(theActualHigh);
		df.setMin(theActualLow);
		df.setReal(true);
		df.setClass(false);
		df.setMean(theActualLow + ((theActualHigh - theActualLow) / 2));

		if (this.getFields() == null) {
			this.setFields(new DataField[] { df });
		} else {
			int len = this.getFields().length;
			DataField[] added = Arrays.copyOf(this.getFields(), len+1);
			added[len] = df;
			this.setFields(added);
		}

		// add a normalized field

		field.setAction(action);
		field.setNormalizedHigh(this.defaultNormalizedRangeHigh);
		field.setNormalizedLow(this.defaultNormalizedRangeLow);
		field.setActualHigh(theActualHigh);
		field.setActualLow(theActualLow);
		field.setName(fieldName);
		field.setOutput(d==FieldDirection.Output || d==FieldDirection.InputOutput);

		getNormalize().getNormalizedFields().add(field);
		return field;
	}

	public void setDefaultNormalizedRange(double low, double high) {
		this.defaultNormalizedRangeLow = low;
		this.defaultNormalizedRangeHigh = high;
	}

	public AnalystField defineClass(String fieldName, FieldDirection d, NormalizationAction action, 
			List<ClassItem> classes) {
		AnalystField field = new AnalystField();

		if (action != NormalizationAction.Equilateral
				&& action != NormalizationAction.OneOf) {
			throw new AnalystError(
					"defineClass can only be used with action type of Equilateral or OneOf");
		}

		field.setAction(action);
		field.setNormalizedHigh(this.defaultNormalizedRangeHigh);
		field.setNormalizedLow(this.defaultNormalizedRangeLow);
		field.setActualHigh(0);
		field.setActualLow(0);
		field.setName(fieldName);
		field.setOutput(d==FieldDirection.Output || d==FieldDirection.InputOutput);
		field.getClasses().addAll(classes);

		getNormalize().getNormalizedFields().add(field);
		return field;
	}

}
