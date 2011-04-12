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
package org.encog.app.analyst.script.prop;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public static final String HEADER_DATASOURCE_sourceFile = "HEADER:DATASOURCE_sourceFile";
	public static final String HEADER_DATASOURCE_rawFile = "HEADER:DATASOURCE_rawFile";
	public static final String HEADER_DATASOURCE_sourceFormat = "HEADER:DATASOURCE_sourceFormat";
	public static final String HEADER_DATASOURCE_sourceHeaders = "HEADER:DATASOURCE_sourceHeaders";
	public static final String SETUP_CONFIG_maxClassCount = "SETUP:CONFIG_maxClassCount";
	public static final String SETUP_CONFIG_allowedClasses = "SETUP:CONFIG_allowedClasses";
	public static final String SETUP_CONFIG_inputHeaders = "SETUP:CONFIG_inputHeaders";
	public static final String SETUP_CONFIG_csvFormat = "SETUP:CONFIG_csvFormat";
	public static final String DATA_CONFIG_goal = "DATA:CONFIG_goal";
	public static final String NORMALIZE_CONFIG_sourceFile = "NORMALIZE:CONFIG_sourceFile";
	public static final String NORMALIZE_CONFIG_targetFile = "NORMALIZE:CONFIG_targetFile";
	public static final String BALANCE_CONFIG_sourceFile = "BALANCE:CONFIG_sourceFile";
	public static final String BALANCE_CONFIG_targetFile = "BALANCE:CONFIG_targetFile";
	public static final String BALANCE_CONFIG_balanceField = "BALANCE:CONFIG_balanceField";
	public static final String BALANCE_CONFIG_countPer = "BALANCE:CONFIG_countPer";
	public static final String RANDOMIZE_CONFIG_sourceFile = "RANDOMIZE:CONFIG_sourceFile";
	public static final String RANDOMIZE_CONFIG_targetFile = "RANDOMIZE:CONFIG_targetFile";
	public static final String SEGREGATE_CONFIG_sourceFile = "SEGREGATE:CONFIG_sourceFile";
	public static final String GENERATE_CONFIG_sourceFile = "GENERATE:CONFIG_sourceFile";
	public static final String GENERATE_CONFIG_targetFile = "GENERATE:CONFIG_targetFile";
	public static final String ML_CONFIG_trainingFile = "ML:CONFIG_trainingFile";
	public static final String ML_CONFIG_evalFile = "ML:CONFIG_evalFile";
	public static final String ML_CONFIG_machineLearningFile = "ML:CONFIG_machineLearningFile";
	public static final String ML_CONFIG_outputFile = "ML:CONFIG_outputFile";
	public static final String ML_CONFIG_type = "ML:CONFIG_type";
	public static final String ML_CONFIG_architecture = "ML:CONFIG_architecture";
	public static final String ML_TRAIN_type = "ML:TRAIN_type";
	public static final String ML_TRAIN_arguments = "ML:TRAIN_arguments";
	public static final String ML_TRAIN_targetError = "ML:TRAIN_targetError";
	public static final String ML_TRAIN_cross = "ML:TRAIN_cross";
	public static final String SERIES_CONFIG_lead = "SERIES:CONFIG_lead";
	public static final String SERIES_CONFIG_lag = "SERIES:CONFIG_lag";
	public static final String SERIES_CONFIG_includeTarget = "SERIES:CONFIG_includeTarget";
	public static final String SERIES_CONFIG_sourceFile = "SERIES:CONFIG_sourceFile";
	public static final String SERIES_CONFIG_targetFile = "SERIES:CONFIG_targetFile";	
	public static final String CLUSTER_CONFIG_sourceFile = "CLUSTER:CONFIG_sourceFile";
	public static final String CLUSTER_CONFIG_targetFile = "CLUSTER:CONFIG_targetFile";
	public static final String CLUSTER_CONFIG_type = "CLUSTER:CONFIG_type";
	public static final String CLUSTER_CONFIG_clusters = "CLUSTER:CONFIG_clusters";

	private final Map<String, String> data = new HashMap<String, String>();

	public void setProperty(String name, String value) {
		data.put(name, value);
	}

	public Object getProperty(String name) {
		return data.get(name);
	}

	public String getPropertyString(String name) {
		if (!data.containsKey(name)) {
			return null;
		}
		return data.get(name).toString();
	}

	public void setProperty(String name, AnalystFileFormat format) {
		if (format == null) {
			data.put(name, "");
		} else {
			data.put(name, ConvertStringConst.analystFileFormat2String(format));
		}
	}

	public void setProperty(String name, boolean b) {
		data.put(name, b ? "t" : "f");
	}

	public void setProperty(String name, File analyzeFile) {
		data.put(name, analyzeFile.toString());

	}

	public void setProperty(String name, URL url) {
		data.put(name, url.toExternalForm());

	}

	public String getPropertyFile(String name) {
		return (String) data.get(name);

	}

	public AnalystFileFormat getPropertyFormat(String name) {
		String value = data.get(name);
		return ConvertStringConst.string2AnalystFileFormat(value);
	}

	public CSVFormat getPropertyCSVFormat(String name) {
		String value = data.get(name);
		AnalystFileFormat code = ConvertStringConst
				.string2AnalystFileFormat(value);
		return ConvertStringConst.convertToCSVFormat(code);
	}

	public URL getPropertyURL(String name) {
		try {
			return new URL(data.get(name));
		} catch (MalformedURLException e) {
			throw new AnalystError(e);
		}
	}

	public void clearFilenames() {
		Object[] array = this.data.keySet().toArray();
		for (int i = 0; i < array.length; i++) {
			String key = (String) array[i];
			if (key.startsWith("SETUP:FILENAMES")) {
				this.data.remove(key);
			}
		}
	}

	public List<String> getFilenames() {
		List<String> result = new ArrayList<String>();
		for (String key : this.data.keySet()) {
			if (key.startsWith("SETUP:FILENAMES")) {
				int index = key.indexOf('_');
				if (index != -1) {
					result.add(key.substring(index + 1));
				}
			}
		}
		return result;
	}

	public void setFilename(String key, String value) {
		String key2 = "SETUP:FILENAMES_" + key;
		this.data.put(key2, value);

	}

	public String getFilename(String file) {
		String key2 = "SETUP:FILENAMES_" + file;

		if (!this.data.containsKey(key2)) {
			throw new AnalystError("Undefined file: " + file);
		}

		return (String) this.data.get(key2);
	}

	public boolean getPropertyBoolean(String name) {
		if (!data.containsKey(name))
			return false;
		else
			return data.get(name).toLowerCase().startsWith("t");
	}

	public int getPropertyInt(String name) {
		try {
			String value = this.data.get(name);
			if (value == null) {
				return 0;
			}
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			throw new AnalystError(ex);
		}
	}

	public void setProperty(String name, int i) {
		this.data.put(name, "" + i);

	}

	public void setProperty(String name, double d) {
		this.data.put(name, CSVFormat.EG_FORMAT.format(d, 5));
	}

	public double getPropertyDouble(String name) {
		String value = this.data.get(name);
		return CSVFormat.EG_FORMAT.parse(value);
	}

	public void setProperty(String name, AnalystGoal goal) {
		switch (goal) {
		case Classification:
			data.put(name, "classification");
			break;
		case Regression:
			data.put(name, "regression");
			break;
		default:
			data.put(name, "");
		}

	}
	
	public Map<String, String> prepareRevert() {
		Map<String, String> result = new HashMap<String, String>();
		result.putAll(this.data);
		return result;
	}
	
	public void performRevert(Map<String, String> revertedData) {
		this.data.clear();
		this.data.putAll(revertedData);
	}

	/** {@inheritDoc} */
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		result.append(getClass().getSimpleName());
		result.append(" :");
		result.append(this.data.toString());
		result.append("]");
		return result.toString();
	}

	public static String toDots(String str) {
		int index1 = str.indexOf(':');
		if( index1==-1)
			return null;
		int index2 = str.indexOf('_');
		if( index2==-1)
			return null;
		String section = str.substring(0,index1);
		String subSection = str.substring(index1+1,index2);
		String name = str.substring(index2+1);
		return section+"."+subSection+"."+name;
	}
}
