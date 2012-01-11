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
package org.encog.app.analyst.script.prop;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.Encog;
import org.encog.app.analyst.AnalystError;
import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.AnalystGoal;
import org.encog.app.analyst.util.ConvertStringConst;
import org.encog.util.csv.CSVFormat;

/**
 * Holds all of the properties for a script. Constants are provided to define
 * "well known" properties.
 * 
 */
public class ScriptProperties {

	/**
	 * Property for: "HEADER:DATASOURCE_sourceFile".
	 */
	public static final String HEADER_DATASOURCE_SOURCE_FILE 
		= "HEADER:DATASOURCE_sourceFile";
	
	/**
	 * Property for: "HEADER:DATASOURCE_rawFile".
	 */
	public static final String HEADER_DATASOURCE_RAW_FILE
		= "HEADER:DATASOURCE_rawFile";
	
	/**
	 * Property for: "HEADER:DATASOURCE_sourceHeaders".
	 */
	public static final String HEADER_DATASOURCE_SOURCE_HEADERS 
		= "HEADER:DATASOURCE_sourceHeaders";
	
	/**
	 * Property for: "SETUP:CONFIG_maxClassCount".
	 */
	public static final String SETUP_CONFIG_MAX_CLASS_COUNT 
		= "SETUP:CONFIG_maxClassCount";
	
	/**
	 * Property for: = "SETUP:CONFIG_allowedClasses". 
	 */
	public static final String SETUP_CONFIG_ALLOWED_CLASSES 
		= "SETUP:CONFIG_allowedClasses";
	
	/**
	 * Property for: "SETUP:CONFIG_inputHeaders". 
	 */
	public static final String SETUP_CONFIG_INPUT_HEADERS 
		= "SETUP:CONFIG_inputHeaders";
	
	/**
	 * Property for: "SETUP:CONFIG_csvFormat". 
	 */
	public static final String SETUP_CONFIG_CSV_FORMAT 
		= "SETUP:CONFIG_csvFormat";
	
	/**
	 * Property for: "DATA:CONFIG_goal". 
	 */
	public static final String DATA_CONFIG_GOAL 
		= "DATA:CONFIG_goal";
	
	/**
	 * Property for: "NORMALIZE:CONFIG_sourceFile". 
	 */
	public static final String NORMALIZE_CONFIG_SOURCE_FILE 
		= "NORMALIZE:CONFIG_sourceFile";
	
	/**
	 * Property for: "NORMALIZE:CONFIG_targetFile". 
	 */
	public static final String NORMALIZE_CONFIG_TARGET_FILE 
		= "NORMALIZE:CONFIG_targetFile";
	
	/**
	 * Property for: "NORMALIZE:CONFIG_missingValues". 
	 */
	public static final String NORMALIZE_MISSING_VALUES 
		= "NORMALIZE:CONFIG_missingValues";
	
	/**
	 * Property for: "BALANCE:CONFIG_sourceFile".
	 */
	public static final String BALANCE_CONFIG_SOURCE_FILE 
		= "BALANCE:CONFIG_sourceFile";
	
	/**
	 * Property for: "BALANCE:CONFIG_targetFile". 
	 */
	public static final String BALANCE_CONFIG_TARGET_FILE 
		= "BALANCE:CONFIG_targetFile";
	
	/**
	 * Property for: "BALANCE:CONFIG_balanceField". 
	 */
	public static final String BALANCE_CONFIG_BALANCE_FIELD 
		= "BALANCE:CONFIG_balanceField";
	
	/**
	 * Property for: "BALANCE:CONFIG_countPer". 
	 */
	public static final String BALANCE_CONFIG_COUNT_PER 
		= "BALANCE:CONFIG_countPer";
	
	/**
	 * Property for: "RANDOMIZE:CONFIG_sourceFile".
	 */
	public static final String RANDOMIZE_CONFIG_SOURCE_FILE 
		= "RANDOMIZE:CONFIG_sourceFile";
	
	/**
	 * Property for: "RANDOMIZE:CONFIG_targetFile". 
	 */
	public static final String RANDOMIZE_CONFIG_TARGET_FILE 
		= "RANDOMIZE:CONFIG_targetFile";
	
	/**
	 * Property for: "SEGREGATE:CONFIG_sourceFile". 
	 */
	public static final String SEGREGATE_CONFIG_SOURCE_FILE 
		= "SEGREGATE:CONFIG_sourceFile";
	
	/**
	 * Property for: "GENERATE:CONFIG_sourceFile". 
	 */
	public static final String GENERATE_CONFIG_SOURCE_FILE 
		= "GENERATE:CONFIG_sourceFile";
	
	/**
	 * Property for: "GENERATE:CONFIG_targetFile". 
	 */
	public static final String GENERATE_CONFIG_TARGET_FILE 
		= "GENERATE:CONFIG_targetFile";
	
	/**
	 * Property for: "ML:CONFIG_trainingFile". 
	 */
	public static final String ML_CONFIG_TRAINING_FILE
		= "ML:CONFIG_trainingFile";
		
	/**
	 * Property for: "ML:CONFIG_evalFile".
	 */
	public static final String ML_CONFIG_EVAL_FILE
		= "ML:CONFIG_evalFile";
	
	/**
	 * Property for: "ML:CONFIG_machineLearningFile".
	 */
	public static final String ML_CONFIG_MACHINE_LEARNING_FILE 
		= "ML:CONFIG_machineLearningFile";
	
	/**
	 * Property for: "ML:CONFIG_outputFile". 
	 */
	public static final String ML_CONFIG_OUTPUT_FILE 
		= "ML:CONFIG_outputFile";
	
	/**
	 * Property for: = ML:CONFIG_type". 
	 */
	public static final String ML_CONFIG_TYPE
		= "ML:CONFIG_type";
	
	/**
	 * Property for: "ML:CONFIG_architecture". 
	 */
	public static final String ML_CONFIG_ARCHITECTURE 
		= "ML:CONFIG_architecture";
	
	/**
	 * Property for "ML:CONFIG_query"
	 */
	public static final String ML_CONFIG_QUERY = "ML:CONFIG_query";
	
	/**
	 * Property for: "ML:TRAIN_type". 
	 */
	public static final String ML_TRAIN_TYPE 
		= "ML:TRAIN_type";
	
	/**
	 * Property for: "ML:TRAIN_arguments". 
	 */
	public static final String ML_TRAIN_ARGUMENTS 
		= "ML:TRAIN_arguments";
	
	/**
	 * Property for: "ML:TRAIN_targetError". 
	 */
	public static final String ML_TRAIN_TARGET_ERROR 
		= "ML:TRAIN_targetError";
	
	/**
	 * Property for: "ML:TRAIN_cross". 
	 */
	public static final String ML_TRAIN_CROSS 
		= "ML:TRAIN_cross";
	
	/**
	 * Property for: "CLUSTER:CONFIG_sourceFile".
	 */
	public static final String CLUSTER_CONFIG_SOURCE_FILE 
		= "CLUSTER:CONFIG_sourceFile";
	
	/**
	 * Property for: "CLUSTER:CONFIG_targetFile". 
	 */
	public static final String CLUSTER_CONFIG_TARGET_FILE 
		= "CLUSTER:CONFIG_targetFile";
	
	/**
	 * Property for: "CLUSTER:CONFIG_type". 
	 */
	public static final String CLUSTER_CONFIG_TYPE 
		= "CLUSTER:CONFIG_type";
	
	/**
	 * Property for: "CLUSTER:CONFIG_clusters". 
	 */
	public static final String CLUSTER_CONFIG_CLUSTERS 
		= "CLUSTER:CONFIG_clusters";

	/**
	 * Convert a key to the dot form.
	 * 
	 * @param str
	 *            The key form.
	 * @return The dot form.
	 */
	public static final String toDots(final String str) {
		final int index1 = str.indexOf(':');
		if (index1 == -1) {
			return null;
		}
		final int index2 = str.indexOf('_');
		if (index2 == -1) {
			return null;
		}
		final String section = str.substring(0, index1);
		final String subSection = str.substring(index1 + 1, index2);
		final String name = str.substring(index2 + 1);
		return section + "." + subSection + "." + name;
	}

	/**
	 * Properties are stored in this map.
	 */
	private final Map<String, String> data = new HashMap<String, String>();

	/**
	 * Clear out all filenames.
	 */
	public final void clearFilenames() {
		final Object[] array = this.data.keySet().toArray();
		for (final Object element : array) {
			final String key = (String) element;
			if (key.startsWith("SETUP:FILENAMES")) {
				this.data.remove(key);
			}
		}
	}

	/**
	 * Get a filename.
	 * @param file The file.
	 * @return The filename.
	 */
	public final String getFilename(final String file) {
		final String key2 = "SETUP:FILENAMES_" + file;

		if (!this.data.containsKey(key2)) {
			throw new AnalystError("Undefined file: " + file);
		}

		return this.data.get(key2);
	}

	/**
	 * Get all filenames.
	 * @return The filenames in a list.
	 */
	public final List<String> getFilenames() {
		final List<String> result = new ArrayList<String>();
		for (final String key : this.data.keySet()) {
			if (key.startsWith("SETUP:FILENAMES")) {
				final int index = key.indexOf('_');
				if (index != -1) {
					result.add(key.substring(index + 1));
				}
			}
		}
		return result;
	}

	/**
	 * Get a property as an object.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The property value.
	 */
	public final Object getProperty(final String name) {
		return this.data.get(name);
	}

	/**
	 * Get a property as a boolean.
	 * @param name The property name.
	 * @return A boolean value.
	 */
	public final boolean getPropertyBoolean(final String name) {
		if (!this.data.containsKey(name)) {
			return false;
		} else {
			return this.data.get(name).toLowerCase().startsWith("t");
		}
	}

	/**
	 * Get a property as a format.
	 * @param name The property name.
	 * @return A format value.
	 */
	public final CSVFormat getPropertyCSVFormat(final String name) {
		final String value = this.data.get(name);
		final AnalystFileFormat code = ConvertStringConst
				.string2AnalystFileFormat(value);
		return ConvertStringConst.convertToCSVFormat(code);
	}

	/**
	 * Get a property as a double.
	 * @param name The property name.
	 * @return A double value.
	 */
	public final double getPropertyDouble(final String name) {
		final String value = this.data.get(name);
		return CSVFormat.EG_FORMAT.parse(value);
	}

	/**
	 * Get a property as a file.
	 * @param name The property name.
	 * @return A file value.
	 */
	public final String getPropertyFile(final String name) {
		return this.data.get(name);

	}

	/**
	 * Get a property as a format.
	 * @param name The property name.
	 * @return A format value.
	 */
	public final AnalystFileFormat getPropertyFormat(final String name) {
		final String value = this.data.get(name);
		return ConvertStringConst.string2AnalystFileFormat(value);
	}

	/**
	 * Get a property as a int.
	 * @param name The property name.
	 * @return A int value.
	 */
	public final int getPropertyInt(final String name) {
		try {
			final String value = this.data.get(name);
			if (value == null) {
				return 0;
			}
			return Integer.parseInt(value);
		} catch (final NumberFormatException ex) {
			throw new AnalystError(ex);
		}
	}

	/**
	 * Get a property as a string.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return The property value.
	 */
	public final String getPropertyString(final String name) {
		if (!this.data.containsKey(name)) {
			return null;
		}
		return this.data.get(name).toString();
	}

	/**
	 * Get a property as a url.
	 * @param name The property name.
	 * @return A url value.
	 */
	public final URL getPropertyURL(final String name) {
		try {
			return new URL(this.data.get(name));
		} catch (final MalformedURLException e) {
			throw new AnalystError(e);
		}
	}

	/**
	 * Perform a revert.
	 * @param revertedData The source data to revert from.
	 */
	public final void performRevert(final Map<String, String> revertedData) {
		this.data.clear();
		this.data.putAll(revertedData);
	}
	
	/**
	 * Prepare a revert. 
	 * @return Data that can be used to revert properties.
	 */
	public final Map<String, String> prepareRevert() {
		final Map<String, String> result = new HashMap<String, String>();
		result.putAll(this.data);
		return result;
	}

	/**
	 * Set a filename.
	 * @param key The key.
	 * @param value The value.
	 */
	public final void setFilename(final String key, final String value) {
		final String key2 = "SETUP:FILENAMES_" + key;
		this.data.put(key2, value);

	}

	/**
	 * Set the property to a format.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param format
	 *            The value of the property.
	 */
	public final void setProperty(final String name,
			final AnalystFileFormat format) {
		if (format == null) {
			this.data.put(name, "");
		} else {
			this.data.put(name,
					ConvertStringConst.analystFileFormat2String(format));
		}
	}

	/**
	 * Set a property.
	 * @param name The name.
	 * @param value The value.
	 */
	public final void setProperty(final String name, final AnalystGoal value) {
		switch (value) {
		case Classification:
			this.data.put(name, "classification");
			break;
		case Regression:
			this.data.put(name, "regression");
			break;
		default:
			this.data.put(name, "");
		}

	}

	/**
	 * Set a property as a boolean.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param b
	 *            The value to set.
	 */
	public final void setProperty(final String name, final boolean b) {
		if (b) {
			this.data.put(name, "t");
		} else {
			this.data.put(name, "f");
		}
	}

	/**
	 * Set a property as a double.
	 * @param name The name of the property.
	 * @param d The value.
	 */
	public final void setProperty(final String name, final double d) {
		this.data.put(name, CSVFormat.EG_FORMAT.format(d, 
					Encog.DEFAULT_PRECISION));
	}

	/**
	 * Get a property as an object.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param f
	 *            The filename value.
	 */
	public final void setProperty(final String name, final File f) {
		this.data.put(name, f.toString());

	}

	/**
	 * Set a property to an int.
	 * @param name The property name.
	 * @param i The value.
	 */
	public final void setProperty(final String name, final int i) {
		this.data.put(name, "" + i);

	}

	/**
	 * Set the property to the specified value.
	 * 
	 * @param name
	 *            The property name.
	 * @param value
	 *            The property value.
	 */
	public final void setProperty(final String name, final String value) {
		this.data.put(name, value);
	}

	/**
	 * Get a property as an object.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param url
	 *            The url of the property.
	 */
	public final void setProperty(final String name, final URL url) {
		this.data.put(name, url.toExternalForm());

	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" :");
		result.append(this.data.toString());
		result.append("]");
		return result.toString();
	}
}
