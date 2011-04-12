/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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

	private DataField[] fields;
	private final AnalystNormalize normalize = new AnalystNormalize();
	private final AnalystSegregate segregate = new AnalystSegregate();
	private final Set<String> generated = new HashSet<String>();
	private final Map<String, AnalystTask> tasks = new HashMap<String, AnalystTask>();
	private final ScriptProperties properties = new ScriptProperties();
	private String basePath;

	public AnalystScript() {
		this.properties.setProperty(ScriptProperties.SETUP_CONFIG_CSV_FORMAT,
				AnalystFileFormat.DECPNT_COMMA);
		this.properties.setProperty(
				ScriptProperties.SETUP_CONFIG_MAX_CLASS_COUNT, 50);
		this.properties
				.setProperty(ScriptProperties.SETUP_CONFIG_ALLOWED_CLASSES,
						"integer,string");
	}

	public void addTask(final AnalystTask task) {
		this.tasks.put(task.getName(), task);
	}

	public void clearTasks() {
		this.tasks.clear();
	}

	public CSVFormat determineInputFormat(final String sourceID) {
		final String rawID = getProperties().getPropertyString(
				ScriptProperties.HEADER_DATASOURCE_RAW_FILE);
		CSVFormat result;

		if (sourceID.equals(rawID)) {
			result = getProperties().getPropertyCSVFormat(
					ScriptProperties.HEADER_DATASOURCE_SOURCE_FORMAT);
		} else {
			result = getProperties().getPropertyCSVFormat(
					ScriptProperties.SETUP_CONFIG_CSV_FORMAT);
		}

		return result;
	}

	public CSVFormat determineOutputFormat() {
		return getProperties().getPropertyCSVFormat(
				ScriptProperties.SETUP_CONFIG_CSV_FORMAT);
	}

	public boolean expectInputHeaders(final String filename) {
		if (isGenerated(filename)) {
			return true;
		} else {
			return this.properties
					.getPropertyBoolean(ScriptProperties.SETUP_CONFIG_INPUT_HEADERS);
		}
	}

	public DataField findDataField(final String name) {
		for (final DataField dataField : this.fields) {
			if (dataField.getName().equalsIgnoreCase(name)) {
				return dataField;
			}
		}

		return null;
	}

	public int findDataFieldIndex(final DataField df) {
		for (int result = 0; result < this.fields.length; result++) {
			if (df == this.fields[result]) {
				return result;
			}
		}
		return -1;
	}

	public AnalystField findNormalizedField(final String name, final int slice) {
		for (final AnalystField field : getNormalize().getNormalizedFields()) {
			if (field.getName().equalsIgnoreCase(name)
					&& (field.getTimeSlice() == slice)) {
				return field;
			}
		}

		return null;
	}

	public String getBasePath() {
		return this.basePath;
	}

	/**
	 * @return the fields
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

	public AnalystTask getTask(final String name) {
		if (!this.tasks.containsKey(name)) {
			return null;
		}
		return this.tasks.get(name);
	}

	public Map<String, AnalystTask> getTasks() {
		return this.tasks;
	}

	public void init() {
		this.normalize.init(this);
	}

	public boolean isGenerated(final String filename) {
		return this.generated.contains(filename);
	}

	public void load(final InputStream stream) {
		final ScriptLoad s = new ScriptLoad(this);
		s.load(stream);
	}

	public void markGenerated(final String filename) {
		this.generated.add(filename);
	}

	public File resolveFilename(final String sourceID) {
		final String name = getProperties().getFilename(sourceID);

		if (this.basePath != null) {
			return new File(this.basePath, name);
		} else {
			return new File(name);
		}
	}

	public void save(final OutputStream stream) {
		final ScriptSave s = new ScriptSave(this);
		s.save(stream);
	}

	public void setBasePath(final String basePath) {
		this.basePath = basePath;
	}

	/**
	 * @param fields
	 *            the fields to set
	 */
	public void setFields(final DataField[] fields) {
		this.fields = fields;
	}
}
